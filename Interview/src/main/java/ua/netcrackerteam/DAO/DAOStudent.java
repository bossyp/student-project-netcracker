package ua.netcrackerteam.DAO;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Public interface DAOStudent - Entity of user Student.
 * Include methods to data transfer between StudentPerson type with DataBase.
 * @author krygin, maxym, Fillipenko
 */
public interface DAOStudent {
    public Collection GetNamesAndContacts() throws SQLException;
}