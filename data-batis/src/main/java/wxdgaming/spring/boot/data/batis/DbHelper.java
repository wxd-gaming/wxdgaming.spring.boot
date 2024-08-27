// package wxdgaming.spring.boot.data.batis;
//
// import jakarta.annotation.PostConstruct;
// import lombok.Getter;
// import lombok.Setter;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.context.annotation.Configuration;
// import wxdgaming.spring.boot.core.InitPrint;
// import wxdgaming.spring.boot.core.Throw;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// /**
//  * 数据源配置
//  *
//  * @author: wxd-gaming(無心道, 15388152619)
//  * @version: 2024-08-02 21:19
//  **/
// @Slf4j
// @Getter
// @Setter
// @Configuration
// @ConfigurationProperties("spring.datasource")
// @ConditionalOnProperty("spring.datasource.url")
// public class DbHelper implements InitPrint {
//
//     private final Pattern DB_NAME_REGEX = Pattern.compile("[^\\/]+(?=(\\?|$))");
//     /**
//      * 默认utf8
//      */
//     public static String DAOCHARACTER = "utf8mb4";
//
//     String url;
//     String username;
//     String password;
//
//     @PostConstruct
//     public void initDb() {
//         String dbName = getDbName(url);
//         createDatabase(dbName);
//     }
//
//     /**
//      * @param dbnameString
//      * @return
//      */
//     public Connection getConnection(String dbnameString) {
//         try {
//             return DriverManager.getConnection(
//                     url.replace(getDbName(url), dbnameString),
//                     username,
//                     password
//             );
//         } catch (SQLException e) {
//             throw Throw.of(e);
//         }
//     }
//
//     public String getDbName(String connectString) {
//         Matcher matcher = DB_NAME_REGEX.matcher(connectString);
//         if (matcher.find()) {
//             return matcher.group();
//         } else {
//             return StringUtils.EMPTY;
//         }
//     }
//
//     /** 创建数据库 , 吃方法创建数据库后会自动使用 use 语句 */
//     public boolean createDatabase(String database) {
//         try (Connection connection = getConnection("INFORMATION_SCHEMA")) {
//             StringBuilder stringBuilder = new StringBuilder();
//             try {
//                 stringBuilder.append("CREATE DATABASE IF NOT EXISTS `")
//                         .append(database.toLowerCase())
//                         .append("` DEFAULT CHARACTER SET ")
//                         .append(DAOCHARACTER)
//                         .append(" COLLATE ")
//                         .append(DAOCHARACTER)
//                         .append("_unicode_ci;");
//                 return connection.createStatement().execute(stringBuilder.toString());
//             } catch (Exception e) {
//                 if (e.getMessage().contains("utf8mb4")) {
//                     DAOCHARACTER = "utf8";
//                     log.warn("数据库 " + database + " 不支持 utf8mb4 格式 重新用 utf8 字符集创建数据库", new RuntimeException());
//                     stringBuilder.setLength(0);
//                     stringBuilder.append("CREATE DATABASE IF NOT EXISTS `")
//                             .append(database.toLowerCase())
//                             .append("` DEFAULT CHARACTER SET ")
//                             .append(DAOCHARACTER)
//                             .append(" COLLATE ")
//                             .append(DAOCHARACTER)
//                             .append("_unicode_ci;");
//                     return connection.createStatement().execute(stringBuilder.toString());
//                 } else {
//                     log.error("创建数据库 {}", database, e);
//                 }
//             }
//         } catch (Exception e) {
//             log.error("创建数据库 {}", database, e);
//         }
//         return false;
//     }
//
// }
