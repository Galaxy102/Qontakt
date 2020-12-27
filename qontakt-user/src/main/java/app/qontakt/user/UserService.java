package app.qontakt.user;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handle all transactions concerning Users
 */
@Component
public class UserService {
    private final VisitRepository visitRepository;

    public UserService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    /**
     * Check if User with the given UID has unterminated Visits
     *
     * @param user_uid UID of the User
     * @return true if User has unterminated Visits
     */
    private boolean hasOpenVisit(String user_uid) {
        return this.visitRepository.findByUserUidAndCheckOutIsNull(user_uid).isPresent();
    }

    /**
     * Create a new Visit for the given User and Lokal
     *
     * @param user_uid  UID of the User
     * @param lokal_uid UID of the Lokal
     * @param time      current time as start of Visit
     * @return true if creation of Visit is successful and User has no unterminated Visits
     */
    @Transactional
    public boolean saveVisit(String user_uid, String lokal_uid, LocalDateTime time) {
        //to be dealt with in Frontend
        if (hasOpenVisit(user_uid)) {
            return false;
        }
        try {
            visitRepository.save(new Visit(user_uid, lokal_uid, time));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all Visits for the given User
     *
     * @param user_uid UID of the User
     * @return List of Visits if the User already has some
     */
    public List<Visit> getVisits(String user_uid) {
        return visitRepository.findAllByUserUid(user_uid).toList();
    }

    /**
     * Get all Visits for the given User at a given Lokal
     *
     * @param lokal_uid UID of the Lokal
     * @param user_uid  UID of the User
     * @return List of Visits
     */
    public List<Visit> getVisits(String lokal_uid, Optional<String> user_uid) {
        Stream<Visit> data = this.visitRepository.findAllByLokalUid(lokal_uid).stream();
        return user_uid
                .map(s -> data.filter(v -> v.getUserUid().equals(s)))
                .orElse(data)
                .collect(Collectors.toList());
    }

    /**
     * Delete a single Visit
     * @param user_uid UID of the User
     * @param visit_uid UID of the Visit
     * @return true if and only if operation was successful
     */
    @Transactional
    public boolean deleteVisit(String user_uid, String visit_uid) {
        Optional<Visit> found = this.visitRepository.findByVisitUid(visit_uid);
        if (found.isEmpty()) {
            return false;
        }
        if (!user_uid.equals(found.get().getUserUid())) {
            throw new IllegalAccessError("Visit does not belong to User.");
        }
        this.visitRepository.delete(found.get());
        return true;
    }
}
