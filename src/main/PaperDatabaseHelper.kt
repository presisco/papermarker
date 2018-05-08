package main

import com.presisco.lazyjdbc.convertion.SimpleJava2SqlConvertion
import main.model.PaperInfo
import org.sqlite.SQLiteConnection
import java.sql.DriverManager
import java.sql.PreparedStatement

class PaperDatabaseHelper {
    init {
        Class.forName("org.sqlite.JDBC")
    }

    private var connection: SQLiteConnection? = null
    private val java2Sql = SimpleJava2SqlConvertion()

    fun setup(filePath: String){
        if (connection != null)
            close()

        connection = DriverManager.getConnection("jdbc:sqlite:$filePath") as SQLiteConnection
        connection!!.autoCommit = false
    }

    fun createTables(){
        val statement = connection!!.createStatement()
        statement.execute(CREATE_PAPER)
        statement.execute(CREATE_AUTHORS)
        statement.execute(CREATE_KEYWORDS)
        statement.execute(CREATE_USERS)
        statement.execute(CREATE_ALGORITHM)
        statement.execute(CREATE_DATA)
        connection!!.commit()
        statement.close()
    }

    fun close(){
        connection!!.close()
    }

    fun relationshipListInsert(preparedStatement: PreparedStatement, title: String, propList: List<String>) {
        for (prop in propList) {
            with(preparedStatement) {
                setString(1, title)
                setString(2, prop)
                addBatch()
            }
        }
        preparedStatement.executeBatch()
    }

    fun addPaper(paperInfo: PaperInfo, userString: String, algorithmString: String, dataString: String) {
        val newPaperStatement = connection!!.prepareStatement(INSERT_PAPER)
        val newKeywordsStatement = connection!!.prepareStatement(INSERT_KEYWORDS)
        val newAuthorsStatement = connection!!.prepareStatement(INSERT_AUTHORS)
        val newUserStatement = connection!!.prepareStatement(INSERT_USERS)
        val newAlgorithmStatement = connection!!.prepareStatement(INSERT_ALGORITHM)
        val newDataStatement = connection!!.prepareStatement(INSERT_DATA)

        val title = paperInfo.title

        with(newPaperStatement) {
            setString(1, paperInfo.title)
            setString(2, paperInfo.year)
            setString(3, paperInfo.abstract)
            setInt(4, paperInfo.downloadCount)
            setInt(5, paperInfo.citedCount)
            setString(6, paperInfo.link)
        }
        newPaperStatement.execute()

        relationshipListInsert(newKeywordsStatement, title, paperInfo.keywords)
        relationshipListInsert(newAuthorsStatement, title, paperInfo.authors)
        relationshipListInsert(newUserStatement, title, userString.replace("，", ",").split(","))
        relationshipListInsert(newAlgorithmStatement, title, algorithmString.replace("，", ",").split(","))
        relationshipListInsert(newDataStatement, title, dataString.replace("，", ",").split(","))

        connection!!.commit()
    }

    companion object {
        const val INSERT_PAPER = "insert into papers values(?,?,?,?,?,?)"
        const val INSERT_KEYWORDS = "insert into keywords values(?,?)"
        const val INSERT_AUTHORS = "insert into authors values(?,?)"
        const val INSERT_USERS = "insert into users values(?,?)"
        const val INSERT_ALGORITHM = "insert into algorithms values(?,?)"
        const val INSERT_DATA = "insert into data values(?,?)"

        const val CREATE_PAPER = "CREATE TABLE IF NOT EXISTS \"papers\" (\n" +
                "  \"title\" TEXT NOT NULL PRIMARY KEY,\n" +
                "  \"year\" TEXT NOT NULL,\n" +
                "  \"abstract\" TEXT NOT NULL,\n" +
                "  \"downloads\" INTEGER NOT NULL,\n" +
                "  \"cited\" INTEGER NOT NULL,\n" +
                "  \"link\" TEXT NOT NULL\n" +
                ");"

        const val CREATE_KEYWORDS = "CREATE TABLE IF NOT EXISTS \"keywords\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"keyword\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"keyword\")\n" +
                ");"

        const val CREATE_AUTHORS = "CREATE TABLE IF NOT EXISTS \"authors\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"author\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"author\")\n" +
                ");"

        const val CREATE_USERS = "CREATE TABLE IF NOT EXISTS \"users\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"user\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"user\")\n" +
                ");\n"

        const val CREATE_ALGORITHM = "CREATE TABLE IF NOT EXISTS \"algorithms\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"algorithm\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"algorithm\")\n" +
                ");\n"

        const val CREATE_DATA = "CREATE TABLE IF NOT EXISTS \"data\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"data\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"data\")\n" +
                ");\n"
    }
}