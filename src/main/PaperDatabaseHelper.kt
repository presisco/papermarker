package main

import com.presisco.lazyjdbc.convertion.SimpleJava2SqlConvertion
import org.sqlite.SQLiteConnection
import java.sql.DriverManager

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
        statement.execute(CREATE_USERS)
        statement.execute(CREATE_ALGORITHM)
        statement.execute(CREATE_DATA)
        connection!!.commit()
        statement.close()
    }

    fun close(){
        connection!!.close()
    }

    fun addPaper(paperPropMap: Map<String,String>){
        val newPaperStatement = connection!!.prepareStatement(INSERT_PAPER)
        val newUserStatement = connection!!.prepareStatement(INSERT_USERS)
        val newAlgorithmStatement = connection!!.prepareStatement(INSERT_ALGORITHM)
        val newDataStatement = connection!!.prepareStatement(INSERT_DATA)

        with(newPaperStatement) {
            setString(1, paperPropMap["title"])
            setString(2, paperPropMap["author"])
            setString(3, paperPropMap["year"])
            setString(4, paperPropMap["link"])
        }

        newPaperStatement.execute()

        for( prop in paperPropMap["users"]!!.replace("，",",").split(",")) {
            with(newUserStatement) {
                setString(1, paperPropMap["title"])
                setString(2, prop)
                addBatch()
            }
        }
        newUserStatement.executeBatch()

        for( prop in paperPropMap["algorithms"]!!.replace("，",",").split(",")) {
            with(newAlgorithmStatement) {
                setString(1, paperPropMap["title"])
                setString(2, prop)
                addBatch()
            }
        }
        newAlgorithmStatement.executeBatch()

        for( prop in paperPropMap["data"]!!.replace("，",",").split(",")) {
            with(newDataStatement) {
                setString(1, paperPropMap["title"])
                setString(2, prop)
                addBatch()
            }
        }
        newDataStatement.executeBatch()

        connection!!.commit()
    }

    companion object {
        const val INSERT_PAPER = "insert into papers values(?,?,?,?)"
        const val INSERT_USERS = "insert into paper_user values(?,?)"
        const val INSERT_ALGORITHM = "insert into paper_algorithm values(?,?)"
        const val INSERT_DATA = "insert into paper_data values(?,?)"

        const val CREATE_PAPER = "CREATE TABLE IF NOT EXISTS \"papers\" (\n" +
                "  \"title\" TEXT NOT NULL PRIMARY KEY,\n" +
                "  \"author\" TEXT NOT NULL,\n" +
                "  \"year\" TEXT NOT NULL,\n" +
                "  \"link\" TEXT NOT NULL\n" +
                ");"

        const val CREATE_USERS = "CREATE TABLE IF NOT EXISTS \"paper_user\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"user\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"user\")\n" +
                ");\n"

        const val CREATE_ALGORITHM = "CREATE TABLE IF NOT EXISTS \"paper_algorithm\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"algorithm\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"algorithm\")\n" +
                ");\n"

        const val CREATE_DATA = "CREATE TABLE IF NOT EXISTS \"paper_data\" (\n" +
                "  \"title\" TEXT NOT NULL,\n" +
                "  \"data\" TEXT NOT NULL,\n" +
                "  PRIMARY KEY (\"title\", \"data\")\n" +
                ");\n"
    }
}