@file:OptIn(ExperimentalUnsignedTypes::class)

import app.cash.sqldelight.db.SqlCursor
import kotlinx.serialization.Serializable

internal interface Repository<T> {
    fun findAll(): List<T>
    suspend fun findById(id: Long): T
    suspend fun first(): T
    suspend fun save(entity: T): T
    val singleEntityMapper: (SqlCursor) -> T
    val listEntityMapper: (SqlCursor) -> List<T>
}

@Serializable
data class CatSaying(
    var id: Long? = null,
    val saying: String
)

@Serializable
data class Paragraph(
    var id: Long? = null,
    val text: String
)

@ExperimentalUnsignedTypes
class CatSayingsRepository(
    val nativeDriver: PostgresNativeDriver,
    override val singleEntityMapper: (SqlCursor) -> CatSaying = {
        it.next()
        CatSaying(
            id = it.getLong(0) ?: -1,
            saying = it.getString(1) ?: throw RuntimeException("Element found without a text!")
        )
    },
    override val listEntityMapper: (SqlCursor) -> List<CatSaying> = {
        val all = mutableListOf<CatSaying>()
        while (it.next()) {
            all.add(
                CatSaying(
                    id = it.getLong(0) ?: -1,
                    saying = it.getString(1) ?: throw RuntimeException("Element found without a text!")
                )
            )
        }
        all.toList()
    }

) : Repository<CatSaying> {

    override fun findAll(): List<CatSaying> =
        nativeDriver.executeSelect(
            sql = "SELECT * from sayings.cat_lines;",
            mapper = listEntityMapper
        ).value

    override suspend fun findById(id: Long): CatSaying = nativeDriver.executeSelect(
        sql = "SELECT * from sayings.cat_lines limit 1 where id = ${id};",
        mapper = singleEntityMapper
    ).value

    override suspend fun first(): CatSaying =
        nativeDriver.executeSelect(
            sql = "SELECT * from sayings.cat_lines limit 1;",
            mapper = singleEntityMapper
        ).value

    override suspend fun save(entity: CatSaying) =
        nativeDriver.executeInsert(null, "INSERT INTO sayings.cat_lines(saying)VALUES ('${entity.saying}')")
            .let { entity }

}

@ExperimentalUnsignedTypes
class ParagraphRepository(
    val nativeDriver: PostgresNativeDriver,
    override val singleEntityMapper: (SqlCursor) -> Paragraph = {
        it.next()
        Paragraph(
            id = it.getLong(0) ?: -1,
            text = it.getString(1) ?: throw RuntimeException("Element found without a text!")
        )
    },
    override val listEntityMapper: (SqlCursor) -> List<Paragraph> = {
        val all = mutableListOf<Paragraph>()
        while (it.next()) {
            all.add(
                Paragraph(
                    id = it.getLong(0) ?: -1,
                    text = it.getString(1) ?: throw RuntimeException("Element found without a text!")
                )
            )
        }
        all.toList()
    }
) : Repository<Paragraph> {
    override fun findAll(): List<Paragraph> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Paragraph {
        TODO("Not yet implemented")
    }

    override suspend fun first(): Paragraph {
        TODO("Not yet implemented")
    }

    override suspend fun save(entity: Paragraph): Paragraph {
        TODO("Not yet implemented")
    }

}