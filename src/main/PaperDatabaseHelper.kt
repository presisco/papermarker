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

    fun relationshipListInsert(preparedStatement: PreparedStatement, id: String, propList: List<String>) {
        for (prop in propList) {
            with(preparedStatement) {
                setString(1, id)
                setString(2, prop)
                addBatch()
            }
        }
        preparedStatement.executeBatch()
    }

    fun addPropInfo(id: String, table: String, propString: String) {
        val newPropStatement = connection!!.prepareStatement(INSERT_PROP.replace("TABLE", table))
        relationshipListInsert(newPropStatement, id, propString.replace("，", ",").split(","))
        connection!!.commit()
    }

    fun delPropInfo(id: String, table: String) {
        val statement = connection!!.createStatement()
        statement.execute(DELETE_PROPS.replace("TABLE", table).replace("ID", id))
        connection!!.commit()
    }

    fun editPropInfo(id: String, table: String, propString: String) {
        delPropInfo(id, table)
        addPropInfo(id, table, propString)
    }

    fun addUsersInfo(id: String, userString: String) = addPropInfo(id, "users", userString)

    fun editUsersInfo(id: String, userString: String) = editPropInfo(id, "users", userString)

    fun addAlgorithmsInfo(id: String, algorithmsString: String) = addPropInfo(id, "algorithms", algorithmsString)

    fun editAlgorithmsInfo(id: String, algorithmsString: String) = editPropInfo(id, "algorithms", algorithmsString)

    fun addDataInfo(id: String, dataString: String) = addPropInfo(id, "data", dataString)

    fun editDataInfo(id: String, dataString: String) = editPropInfo(id, "data", dataString)

    fun addPaper(paperInfo: PaperInfo) {
        val newPaperStatement = connection!!.prepareStatement(INSERT_PAPER)
        val newKeywordsStatement = connection!!.prepareStatement(INSERT_KEYWORDS)
        val newAuthorsStatement = connection!!.prepareStatement(INSERT_AUTHORS)
        val newInstitutesStatement = connection!!.prepareStatement(INSERT_INSTITUTES)

        val id = paperInfo.id

        with(newPaperStatement) {
            setString(1, paperInfo.id)
            setString(2, paperInfo.title)
            setString(3, paperInfo.year)
            setString(4, paperInfo.publisher)
            setString(5, paperInfo.type)
            setString(6, paperInfo.abstract)
            setInt(7, paperInfo.downloadCount)
            setInt(8, paperInfo.citedCount)
            setString(9, paperInfo.link)
        }
        newPaperStatement.execute()

        relationshipListInsert(newKeywordsStatement, id, paperInfo.keywords)
        relationshipListInsert(newAuthorsStatement, id, paperInfo.authors)
        relationshipListInsert(newInstitutesStatement, id, paperInfo.institutes)

        connection!!.commit()
    }

    fun delPaper(paperInfo: PaperInfo) {
        val statement = connection!!.createStatement()
        statement.execute(DELETE_PAPER.replace("ID", paperInfo.id))
        statement.execute(DELETE_PROPS.replace("TABLE", "authors").replace("ID", paperInfo.id))
        statement.execute(DELETE_PROPS.replace("TABLE", "institutes").replace("ID", paperInfo.id))
        statement.execute(DELETE_PROPS.replace("TABLE", "keywords").replace("ID", paperInfo.id))
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

    fun readPaperIds() = sql2StringList(SELECT_PAPER_IDS)

    fun readPaperIdsByKeyword(keyword: String) = sql2StringList(SELECT_PAPER_IDS_BY_KEYWORD.replace("KEYWORD", keyword))

    fun readPaperIdsWithSql(sql: String) = sql2StringList(sql)

    fun readAuthorsForPaper(id: String) = sql2StringList(SELECT_AUTHORS.replace("ID", id))

    fun readKeywordsForPaper(id: String) = sql2StringList(SELECT_KEYWORDS.replace("ID", id))

    fun readInstitutesForPaper(id: String) = sql2StringList(SELECT_INSTITUTES.replace("ID", id))

    fun readPaperInfo(id: String): PaperInfo {
        val statement = connection!!.createStatement()
        val resultSet = statement.executeQuery(SELECT_PAPER.replace("ID", id))
        resultSet.next()
        val paperInfoRow = sql2Java.toArray(resultSet)

        statement.close()
        return PaperInfo(
                id = id,
                title = paperInfoRow[1] as String,
                authors = readAuthorsForPaper(id),
                keywords = readKeywordsForPaper(id),
                year = paperInfoRow[2] as String,
                type = paperInfoRow[4] as String,
                abstract = paperInfoRow[5] as String,
                citedCount = paperInfoRow[7] as Int,
                downloadCount = paperInfoRow[6] as Int,
                link = paperInfoRow[8] as String,
                refList = arrayListOf(),
                institutes = readInstitutesForPaper(id),
                publisher = paperInfoRow[3] as String
        )
    }

    fun readUsersForPaper(id: String) = sql2StringList(SELECT_USERS.replace("ID", id))
    fun readAlgorithmsForPaper(id: String) = sql2StringList(SELECT_ALGORITHMS.replace("ID", id))
    fun readDataForPaper(id: String) = sql2StringList(SELECT_DATA.replace("ID", id))

    companion object {
        const val SELECT_PAPER = "select * from papers where id = 'ID'"
        const val SELECT_PAPER_IDS = "select id from papers"
        const val SELECT_PAPER_IDS_BY_KEYWORD = "select papers.id from papers, keywords where keyword = 'KEYWORD' and keywords.id = papers.id"
        const val SELECT_AUTHORS = "select author from authors where id = 'ID'"
        const val SELECT_KEYWORDS = "select keyword from keywords where id = 'ID'"
        const val SELECT_INSTITUTES = "select institute from institutes where id = 'ID'"
        const val SELECT_USERS = "select user from users where id = 'ID'"
        const val SELECT_ALGORITHMS = "select algorithm from algorithms where id = 'ID'"
        const val SELECT_DATA = "select data from data where id = 'ID'"

        const val DELETE_PAPER = "delete from papers where id = 'ID'"
        const val DELETE_PROPS = "delete from TABLE where id = 'ID'"

        const val INSERT_PAPER = "insert into papers values(?,?,?,?,?,?,?,?,?)"
        const val INSERT_KEYWORDS = "insert into keywords values(?,?)"
        const val INSERT_AUTHORS = "insert into authors values(?,?)"
        const val INSERT_INSTITUTES = "insert into institutes values(?,?)"
        const val INSERT_PROP = "insert into TABLE values(?,?)"

        const val CREATE_PAPER = "CREATE TABLE IF NOT EXISTS \"papers\" (\n" +
                "  \"id\" TEXT NOT NULL PRIMARY KEY,\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"year\" TEXT NOT NULL,\n" +
                "  \"publisher\" TEXT NOT NULL,\n" +
                "  \"type\" TEXT NOT NULL,\n" +
                "  \"abstract\" TEXT NOT NULL,\n" +
                "  \"downloads\" INTEGER NOT NULL,\n" +
                "  \"cited\" INTEGER NOT NULL,\n" +
                "  \"link\" TEXT NOT NULL\n" +
                ");"

        const val CREATE_KEYWORDS = "CREATE TABLE IF NOT EXISTS \"keywords\" (\n" +
                "  \"id\" TEXT NOT NULL,\n" +
                "  \"keyword\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"id\", \"keyword\")\n" +
                ");"

        const val CREATE_AUTHORS = "CREATE TABLE IF NOT EXISTS \"authors\" (\n" +
                "  \"id\" TEXT NOT NULL,\n" +
                "  \"author\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"id\", \"author\")\n" +
                ");"

        const val CREATE_INSTITUTES = "CREATE TABLE IF NOT EXISTS \"institutes\" (\n" +
                "  \"id\" TEXT NOT NULL,\n" +
                "  \"institute\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"id\", \"institute\")\n" +
                ");"

        const val CREATE_USERS = "CREATE TABLE IF NOT EXISTS \"users\" (\n" +
                "  \"id\" TEXT NOT NULL,\n" +
                "  \"user\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"id\", \"user\")\n" +
                ");\n"

        const val CREATE_ALGORITHM = "CREATE TABLE IF NOT EXISTS \"algorithms\" (\n" +
                "  \"id\" TEXT NOT NULL,\n" +
                "  \"algorithm\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"id\", \"algorithm\")\n" +
                ");\n"

        const val CREATE_DATA = "CREATE TABLE IF NOT EXISTS \"data\" (\n" +
                "  \"id\" TEXT NOT NULL,\n" +
                "  \"data\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"id\", \"data\")\n" +
                ");\n"
    }
}