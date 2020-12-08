package app.qontakt.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

/**
 * Repository to save data of Visits
 */
@Repository
public interface VisitRepository extends CrudRepository<Visit, String> {
    /**
     * Find all Visits with a given User
     *
     * @param user_uid UID of the User
     * @return Streamable of all Visits belonging to the given User
     */
    Streamable<Visit> findAllByUserUid(String user_uid);

    /**
     * Find all unterminated Visits for a given User
     *
     * @param user_uid UID of the User
     * @return Streamable of all unterminated Visits belonging to the given User
     */
    Streamable<Visit> findAllByUserUidAndEndIsNull(String user_uid);

    /**
     * Find all Visits for a given Lokal
     *
     * @param lokal_uid UID of the Lokal
     * @return Streamable of all Visits belonging to the given Lokal
     */
    Streamable<Visit> findAllByLokalUid(String lokal_uid);
}