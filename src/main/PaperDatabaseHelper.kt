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

    fun addPropInfo(title: String, table: String, propString: String) {
        val newPropStatement = connection!!.prepareStatement(INSERT_PROP.replace("TABLE", table))
        relationshipListInsert(newPropStatement, title, propString.replace("，", ",").split(","))
        connection!!.commit()
    }

    fun delPropInfo(title: String, table: String) {
        val statement = connection!!.createStatement()
        statement.execute(DELETE_PROPS.replace("TABLE", table).replace("TITLE", title))
        connection!!.commit()
    }

    fun editPropInfo(title: String, table: String, propString: String) {
        delPropInfo(title, table)
        addPropInfo(title, table, propString)
    }

    fun addUsersInfo(title: String, userString: String) = addPropInfo(title, "users", userString)

    fun editUsersInfo(title: String, userString: String) = editPropInfo(title, "users", userString)

    fun addAlgorithmsInfo(title: String, algorithmsString: String) = addPropInfo(title, "algorithms", algorithmsString)

    fun editAlgorithmsInfo(title: String, algorithmsString: String) = editPropInfo(title, "algorithms", algorithmsString)

    fun addDataInfo(title: String, dataString: String) = addPropInfo(title, "data", dataString)

    fun editDataInfo(title: String, dataString: String) = editPropInfo(title, "data", dataString)

    fun addPaper(paperInfo: PaperInfo) {
        val newPaperStatement = connection!!.prepareStatement(INSERT_PAPER)
        val newKeywordsStatement = connection!!.prepareStatement(INSERT_KEYWORDS)
        val newAuthorsStatement = connection!!.prepareStatement(INSERT_AUTHORS)
        val newInstitutesStatement = connection!!.prepareStatement(INSERT_INSTITUTES)

        val title = paperInfo.title

        with(newPaperStatement) {
            setString(1, paperInfo.title)
            setString(2, paperInfo.year)
            setString(3, paperInfo.publisher)
            setString(4, paperInfo.abstract)
            setInt(5, paperInfo.downloadCount)
            setInt(6, paperInfo.citedCount)
            setString(7, paperInfo.link)
        }
        newPaperStatement.execute()

        relationshipListInsert(newKeywordsStatement, title, paperInfo.keywords)
        relationshipListInsert(newAuthorsStatement, title, paperInfo.authors)
        relationshipListInsert(newInstitutesStatement, title, paperInfo.institutes)

        connection!!.commit()
    }

    fun delPaper(paperInfo: PaperInfo) {
        val statement = connection!!.createStatement()
        statement.execute(DELETE_PAPER.replace("TITLE", paperInfo.title))
        statement.execute(DELETE_PROPS.replace("TABLE", "authors").replace("TITLE", paperInfo.title))
        statement.execute(DELETE_PROPS.replace("TABLE", "institutes").replace("TITLE", paperInfo.title))
        statement.execute(DELETE_PROPS.replace("TABLE", "keywords").replace("TITLE", paperInfo.title))
        connection!!.commit()
    }

    fun editPaper(paperInfo: PaperInfo) {
        delPaper(paperInfo)
        addPaper(paperInfo)
    }

    fun sql2StringList(sql: String): List<String> {
        val statement = connection!!.createStatement()
        val resultSet = statement.executeQuery(sql)
        val stringList = ArrayList<String>()
        while (resultSet.next()) {
            val dataRow = sql2Java.toArray(resultSet)
            stringList.add(dataRow[0] as String)
        }
        resultSet.close()
        statement.close()
        return stringList
    }

    fun readPaperTitles(sql: String) = sql2StringList(sql)

    fun readPaperTitles() = readPaperTitles(SELECT_PAPER_TITLES)

    fun readPaperTitlesByKeyword(keyword: String) = readPaperTitles(SELECT_PAPER_TITLES_BY_KEYWORD.replace("KEYWORD", keyword))

    fun readAuthorsForPaper(title: String) = sql2StringList(SELECT_AUTHORS.replace("TITLE", title))

    fun readKeywordsForPaper(title: String) = sql2StringList(SELECT_KEYWORDS.replace("TITLE", title))

    fun readInstitutesForPaper(title: String) = sql2StringList(SELECT_INSTITUTES.replace("TITLE", title))

    fun readPaperInfo(title: String): PaperInfo{
        val statement = connection!!.createStatement()
        val resultSet = statement.executeQuery(SELECT_PAPER.replace("TITLE",title))
        resultSet.next()
        val paperInfoRow = sql2Java.toArray(resultSet)

        statement.close()
        return PaperInfo(
                title = title,
                authors = readAuthorsForPaper(title),
                keywords = readKeywordsForPaper(title),
                year = paperInfoRow[1] as String,
                abstract = paperInfoRow[3] as String,
                citedCount = paperInfoRow[5] as Int,
                downloadCount = paperInfoRow[4] as Int,
                link = paperInfoRow[6] as String,
                refList = arrayListOf(),
                institutes = readInstitutesForPaper(title),
                publisher = paperInfoRow[2] as String
        )
    }

    fun readUsersForPaper(title: String) = sql2StringList(SELECT_USERS.replace("TITLE", title))
    fun readAlgorithmsForPaper(title: String) = sql2StringList(SELECT_ALGORITHMS.replace("TITLE", title))
    fun readDataForPaper(title: String) = sql2StringList(SELECT_DATA.replace("TITLE", title))

    companion object {
        const val SELECT_PAPER = "select * from papers where title = 'TITLE'"
        const val SELECT_PAPER_TITLES = "select title from papers"
        const val SELECT_PAPER_TITLES_BY_KEYWORD = "select papers.title from papers, keywords where keyword = 'KEYWORD' and keywords.title = papers.title"
        const val SELECT_AUTHORS = "select author from authors where title = 'TITLE'"
        const val SELECT_KEYWORDS = "select keyword from keywords where title = 'TITLE'"
        const val SELECT_INSTITUTES = "select institute from institutes where title = 'TITLE'"
        const val SELECT_USERS = "select user from users where title = 'TITLE'"
        const val SELECT_ALGORITHMS = "select algorithm from algorithms where title = 'TITLE'"
        const val SELECT_DATA = "select data from data where title = 'TITLE'"

        const val DELETE_PAPER = "delete from papers where title = 'TITLE'"
        const val DELETE_PROPS = "delete from TABLE where title = 'TITLE'"

        const val INSERT_PAPER = "insert into papers values(?,?,?,?,?,?,?)"
        const val INSERT_KEYWORDS = "insert into keywords values(?,?)"
        const val INSERT_AUTHORS = "insert into authors values(?,?)"
        const val INSERT_INSTITUTES = "insert into institutes values(?,?)"
        const val INSERT_PROP = "insert into TABLE values(?,?)"

        const val CREATE_PAPER = "CREATE TABLE IF NOT EXISTS \"papers\" (\n" +
                "  \"title\" TEXT NOT NULL PRIMARY KEY,\n" +
                "  \"year\" TEXT NOT NULL,\n" +
                "  \"publisher\" TEXT NOT NULL,\n" +
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