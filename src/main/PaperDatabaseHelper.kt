package main

import com.presisco.lazyjdbc.convertion.SimpleJava2SqlConvertion
import com.presisco.lazyjdbc.convertion.SimpleSql2JavaConvertion
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
    private val sql2Java = SimpleSql2JavaConvertion()

    fun setup(filePath: String){
        if (connection != null)
            close()

        connection = DriverManager.getConnection("jdbc:sqlite:$filePath") as SQLiteConnection
        connection!!.autoCommit = false

        createTables()

        println("database $filePath loaded!")
    }

    fun createTables(){
        val statement = connection!!.createStatement()
        statement.execute(CREATE_PAPER)
        statement.execute(CREATE_AUTHORS)
        statement.execute(CREATE_KEYWORDS)
        statement.execute(CREATE_INSTITUTES)
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

    fun addUsersInfo(title: String, userString: String) {
        val newUserStatement = connection!!.prepareStatement(INSERT_USERS)
        relationshipListInsert(newUserStatement, title, userString.replace("，", ",").split(","))
        connection!!.commit()
    }

    fun addAlgorithmsInfo(title: String, algorithmString: String) {
        val newAlgorithmStatement = connection!!.prepareStatement(INSERT_ALGORITHM)
        relationshipListInsert(newAlgorithmStatement, title, algorithmString.replace("，", ",").split(","))
        connection!!.commit()
    }

    fun addDataInfo(title: String, dataString: String) {
        val newDataStatement = connection!!.prepareStatement(INSERT_DATA)
        relationshipListInsert(newDataStatement, title, dataString.replace("，", ",").split(","))
        connection!!.commit()
    }

    fun addPaper(paperInfo: PaperInfo) {
        val newPaperStatement = connection!!.prepareStatement(INSERT_PAPER)
        val newKeywordsStatement = connection!!.prepareStatement(INSERT_KEYWORDS)
        val newAuthorsStatement = connection!!.prepareStatement(INSERT_AUTHORS)
        val newInstitutesStatement = connection!!.prepareStatement(INSERT_INSTITUTES)

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
        relationshipListInsert(newInstitutesStatement, title, paperInfo.institutes)

        connection!!.commit()
    }

    fun readPaperTitles(): List<String> {
        val statement = connection!!.createStatement()
        val resultSet = statement.executeQuery(SELECT_PAPER_TITLES)
        val titleList = ArrayList<String>()
        while (resultSet.next()) {
            val dataRow = sql2Java.toArray(resultSet)
            titleList.add(dataRow[0] as String)
        }
        statement.close()
        return titleList
    }

    /*
    fun readPaperInfo(title: String): PaperInfo{
        val statement = connection!!.createStatement()
        val resultSet = statement.executeQuery(SELECT_PAPER.replace("TITLE",title))
        resultSet.next()
        val paperInfoRow = sql2Java.toArray(resultSet)

        statement.close()
        return PaperInfo(
                title = title,
                authors =
        )
    }
    */

    companion object {
        const val SELECT_PAPERS = "select * from papers"
        const val SELECT_PAPER = "select * from papers where title = 'TITLE'"
        const val SELECT_PAPER_TITLES = "select title from papers"
        const val SELECT_AUTHORS = "select * from authors where title = 'TITLE'"
        const val SELECT_KEYWORDS = "select * from keywords where title = 'TITLE'"

        const val INSERT_PAPER = "insert into papers values(?,?,?,?,?,?)"
        const val INSERT_KEYWORDS = "insert into keywords values(?,?)"
        const val INSERT_AUTHORS = "insert into authors values(?,?)"
        const val INSERT_INSTITUTES = "insert into institutes values(?,?)"
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

        const val CREATE_INSTITUTES = "CREATE TABLE IF NOT EXISTS \"institutes\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"institute\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"institute\")\n" +
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