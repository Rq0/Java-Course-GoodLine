import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;

class AccountDAO {
    private static final Logger log = LogManager.getLogger(Main.class.getName());
    void addAccount(Account account, DbContext dbContext) {
        String table = "Accounts";
        SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd") {
            {
                setLenient(false);
            }
        };
        String paramsValues = String.format("%s, %s, %s, '%s', '%s'", account.id, account.resourceId, account.vol, newDate.format(account.ds), newDate.format(account.de));
        dbContext.insert(table, paramsValues);
        log.info("Добавлен аккаунт {} в бд", account.id);
    }

    Account selectAccount(int accountId, DbContext dbContext) {
        String table = "Accounts";
        String params = "ID, RESOURCEID, VOL, DS, DE";
        String filter = "where ID = " + accountId;
        ResultSet selected = dbContext.select(table, params, filter);

        try {
            selected.next();
            return new Account(
                    selected.getInt(1),
                    selected.getInt(2),
                    selected.getInt(3),
                    selected.getDate(4),
                    selected.getDate(5));
        } catch (Exception e) {
            log.error("SelectAccount {} error; {}",accountId, e.getMessage());
            System.exit(4042);
        }
        return null;
    }
}
