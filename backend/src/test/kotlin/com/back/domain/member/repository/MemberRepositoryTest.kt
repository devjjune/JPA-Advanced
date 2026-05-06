package com.back.domain.member.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `findById()`() {
        val member = memberRepository.findById(1).get()
        assertThat(member.id).isEqualTo(1)
    }

    @Test
    fun `findQById()`() {
        val member = memberRepository.findQById(1).getOrThrow()
        assertThat(member.id).isEqualTo(1)
    }
}