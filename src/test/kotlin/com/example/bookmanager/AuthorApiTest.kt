package com.example.bookmanager

import com.example.bookmanager.author.api.dto.CreateAuthorRequest
import com.example.bookmanager.author.api.dto.UpdateAuthorRequest
import com.example.bookmanager.book.api.dto.CreateBookRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDate


class AuthorApiTest : BaseIntegrationTest() {

    // --- A. 著者に関するテスト ---

    @Nested
    @DisplayName("A-1, A-2: 著者の登録")
    inner class CreateAuthor {
        @Test
        @DisplayName("A-1 正常系: 著者4名を登録できること")
        fun createAuthorsSuccess() {
            val authors = listOf(
                CreateAuthorRequest("Author1", LocalDate.of(1980, 1, 1)),
                CreateAuthorRequest("Author2", LocalDate.of(1990, 5, 5)),
                CreateAuthorRequest("Author3", LocalDate.of(2000, 12, 31)),
                CreateAuthorRequest("Author4", LocalDate.of(1995, 7, 7))
            )

            authors.forEachIndexed { index, request ->
                mockMvc.perform(
                    post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.id").value(index + 1)) // IDが1から順に振られること
                    .andExpect(jsonPath("$.name").value(request.name))
            }
        }

        @Test
        @DisplayName("A-2-1 異常系: 未来の日付の場合エラー")
        fun createFutureDate() {
            val request = CreateAuthorRequest("FutureMan", LocalDate.now().plusDays(1))

            mockMvc.perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
                // 統一されたエラー形式のチェック
                .andExpect(jsonPath("$.errors[0].field").value("business_rule"))
                .andExpect(jsonPath("$.errors[0].reason").value("birthDate must be today or in the past"))
        }
    }

    @Nested
    @DisplayName("A-3, A-4: 著者の更新")
    inner class UpdateAuthor {
        @Test
        @DisplayName("A-3 正常系: 名前と生年月日を更新できる")
        fun updateSuccess() {
            createAuthor("Original", LocalDate.of(1990, 1, 1)) // ID=1作成

            val updateReq = UpdateAuthorRequest("UpdatedName", LocalDate.of(1991, 2, 2))

            mockMvc.perform(
                patch("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReq))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.birthDate").value("1991-02-02"))
        }

        @Test
        @DisplayName("A-4-1 異常系: 未来の日付へ更新はエラー")
        fun updateFutureDate() {
            createAuthor("Original", LocalDate.of(1990, 1, 1))

            val updateReq = UpdateAuthorRequest(null, LocalDate.now().plusDays(1))

            mockMvc.perform(
                patch("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReq))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.errors[0].reason").exists())
        }

        @Test
        @DisplayName("A-4-2 異常系: 存在しない著者IDの更新は404")
        fun updateNotFound() {
            val updateReq = UpdateAuthorRequest("Ghost", null)

            mockMvc.perform(
                patch("/authors/9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReq))
            )
                .andExpect(status().isNotFound)
        }
    }

    // --- C. 著者に紐づく書籍取得 ---

    @Nested
    @DisplayName("C. 著者に紐づく書籍取得")
    inner class GetAuthorBooks {
        @Test
        @DisplayName("C-1 正常系: 著者が執筆した書籍リストが取得できる")
        fun getBooksByAuthor() {
            // Setup: 著者1を作成し、本を2冊登録
            createAuthor("Author1", LocalDate.of(1980, 1, 1)) // ID=1
            createBook("Book1", 1000, listOf(1))
            createBook("Book2", 2000, listOf(1))

            mockMvc.perform(get("/authors/1/books"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Book1"))
                .andExpect(jsonPath("$[1].title").value("Book2"))
        }

        @Test
        @DisplayName("C-2-1(1-1) 異常系: 存在しない著者は404")
        fun getBooksByNonExistentAuthor() {
            mockMvc.perform(get("/authors/9999/books"))
                .andExpect(status().isNotFound)
        }

        @Test
        @DisplayName("C-2-1(2-1) 正常系: 本を持たない著者は空リスト")
        fun getBooksEmpty() {
            createAuthor("NoBookAuthor", LocalDate.of(1980, 1, 1)) // ID=1

            mockMvc.perform(get("/authors/1/books"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(0))
        }
    }

    // --- Helper Methods ---
    private fun createAuthor(name: String, birthDate: LocalDate) {
        val req = CreateAuthorRequest(name, birthDate)
        mockMvc.perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
    }

    private fun createBook(title: String, price: Int, authorIds: List<Long>) {
        val req = CreateBookRequest(title, BigDecimal(price), authorIds, "PUBLISHED")
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
    }
}