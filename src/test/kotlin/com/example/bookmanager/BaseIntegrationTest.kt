package com.example.bookmanager

import com.example.bookmanager.jooq.Tables.*
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import tools.jackson.databind.json.JsonMapper

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: JsonMapper

    @Autowired
    lateinit var dsl: DSLContext

    @BeforeEach
    fun cleanupDatabase() {
        // 外部キー制約があるため、子テーブルから順に削除
        dsl.deleteFrom(BOOK_AUTHORS).execute()
        dsl.deleteFrom(BOOKS).execute()
        dsl.deleteFrom(AUTHORS).execute()

        // IDの自動採番(シーケンス)を 1 にリセット
        // これにより、テスト実行時は常に ID=1 から始まります
        dsl.execute("ALTER SEQUENCE authors_id_seq RESTART WITH 1")
        dsl.execute("ALTER SEQUENCE books_id_seq RESTART WITH 1")
    }
}