package com.example.bookmanager

import com.example.bookmanager.author.api.dto.CreateAuthorRequest
import com.example.bookmanager.book.api.dto.CreateBookRequest
import com.example.bookmanager.book.api.dto.UpdateBookRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDate

class BookApiTest : BaseIntegrationTest() {

    // --- B. 書籍に関するテスト ---

    @Nested
    @DisplayName("B-1, B-2: 書籍の登録")
    inner class CreateBook {
        @Test
        @DisplayName("B-1 正常系: 書籍を3冊登録（単著、共著、その他）")
        fun createBooksSuccess() {
            // 事前準備: 著者3名作成 (ID=1, 2, 3)
            createAuthors(3)

            // 1冊目: 存在する著者IDを1つ
            val book1 = CreateBookRequest("Book1", BigDecimal(1000), listOf(1), "UNPUBLISHED")
            // 2冊目: 存在する著者IDを3つ (ID=1を含む)
            val book2 = CreateBookRequest("Book2", BigDecimal(2000), listOf(1, 2, 3), "PUBLISHED")
            // 3冊目: チェック用
            val book3 = CreateBookRequest("Book3", BigDecimal(3000), listOf(2), "UNPUBLISHED")

            listOf(book1, book2, book3).forEach { req ->
                mockMvc.perform(
                    post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                ).andExpect(status().isCreated)
            }
        }

        @Test
        @DisplayName("B-2-1 異常系: 価格が-1の場合はエラー")
        fun createInvalidPrice() {
            createAuthors(1)
            val req = CreateBookRequest("BadPrice", BigDecimal(-1), listOf(1), "UNPUBLISHED")

            mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.errors[0].reason").value("price must be >= 0"))
        }

        @Test
        @DisplayName("B-2-2 異常系: 存在しない著者IDを指定")
        fun createNonExistentAuthor() {
            val req = CreateBookRequest("GhostAuth", BigDecimal(1000), listOf(9999), "UNPUBLISHED")

            mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest)
                // 実装に合わせて "Business rule violation" 等のエラーを確認
                .andExpect(jsonPath("$.errors[0].field").exists())
        }

        @Test
        @DisplayName("B-2-4 異常系: 存在と非存在の混合")
        fun createMixedAuthors() {
            createAuthors(1) // ID=1作成
            val req = CreateBookRequest("MixAuth", BigDecimal(1000), listOf(1, 9999), "UNPUBLISHED")

            mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    @DisplayName("B-3, B-4: 書籍の更新")
    inner class UpdateBook {
        @Test
        @DisplayName("B-3 正常系: タイトル、価格、ステータス、著者を更新")
        fun updateSuccess() {
            createAuthors(2)
            createBook("Original", 1000, listOf(1), "UNPUBLISHED") // BookID=1

            val updateReq = UpdateBookRequest(
                title = "UpdatedTitle",
                price = BigDecimal(5000),
                publicationStatus = "PUBLISHED",
                authorIds = listOf(2)
            )

            mockMvc.perform(
                patch("/books/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReq))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.title").value("UpdatedTitle"))
                .andExpect(jsonPath("$.publicationStatus").value("PUBLISHED"))
                .andExpect(jsonPath("$.authorIds[0]").value(2))
        }

        @Test
        @DisplayName("B-4-1 異常系: 出版済み→未出版への変更は禁止")
        fun updatePublishedToUnpublished() {
            createAuthors(1)
            createBook("PublishedBook", 1000, listOf(1), "PUBLISHED") // BookID=1

            val updateReq = UpdateBookRequest(publicationStatus = "UNPUBLISHED")

            mockMvc.perform(
                patch("/books/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReq))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.errors[0].reason").value("published book cannot be changed to unpublished"))
        }

        @Test
        @DisplayName("B-4-2 異常系: 出版ステータスが不正値 (400 Bad Request)")
        fun updateInvalidStatus() {
            createAuthors(1)
            createBook("Book", 1000, listOf(1), "UNPUBLISHED")

            // Enumにない値を送ると、Spring標準では400 (MethodArgumentNotValidExceptionではないがBadRequest) になる
            val invalidJson = """{"publicationStatus": "INVALID_STATUS"}"""

            mockMvc.perform(
                patch("/books/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("B-4-3 異常系: 更新で価格-1")
        fun updateInvalidPrice() {
            createAuthors(1)
            createBook("Book", 1000, listOf(1), "UNPUBLISHED")
            val req = UpdateBookRequest(price = BigDecimal(-1))

            mockMvc.perform(patch("/books/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("B-4-5 異常系: 存在しない書籍の更新は404")
        fun updateNonExistentBook() {
            val req = UpdateBookRequest(title = "Ghost")
            mockMvc.perform(patch("/books/9999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound)
        }
    }

    // --- D. その他 ---

    @Nested
    @DisplayName("D. その他")
    inner class OtherTests {
        @Test
        @DisplayName("D-1-1 著者IDを重複指定した場合、重複排除されて登録される")
        fun createDuplicateAuthorIds() {
            createAuthors(1) // ID=1

            // ID=1 を2回指定
            val req = CreateBookRequest("DupAuth", BigDecimal(1000), listOf(1, 1), "UNPUBLISHED")

            mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.authorIds.length()").value(1)) // 2つ指定したが1つになっていること
                .andExpect(jsonPath("$.authorIds[0]").value(1))
        }
    }

    // --- Helper ---
    private fun createAuthors(count: Int) {
        for (i in 1..count) {
            val req = CreateAuthorRequest("Author$i", LocalDate.of(1990, 1, 1))
            mockMvc.perform(post("/authors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
        }
    }

    private fun createBook(title: String, price: Int, authorIds: List<Long>, status: String) {
        val req = CreateBookRequest(title, BigDecimal(price), authorIds, status)
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
    }
}