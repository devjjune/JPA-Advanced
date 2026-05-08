package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import com.back.domain.member.entity.QMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class MemberRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): MemberRepositoryCustom{
    override fun findQById(id: Int): Member? {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.id.eq(id)) // where member.id = id
            .fetchOne() // limit 1
    }

    override fun findQByUsername(username: String): Member? {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetchOne() // limit 1
    }

    override fun findQByIdIn(ids: List<Int>): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.id.`in`(ids))
            .fetch()
    }

    override fun findQByUsernameAndNickname(username: String, nickname: String): Member? {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(member.nickname.eq(nickname))
            )
            .fetchOne()
    }

    override fun findQByUsernameOrNickname(username: String, nickname: String): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .or(member.nickname.eq(nickname))
            )
            .fetch()
    }

    override fun findQByUsernameAndEitherPasswordOrNickname(
        username: String,
        password: String,
        nickname: String
    ): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(
                        member.password.eq(password)
                            .or(member.nickname.eq(nickname))
                    )
            )
            .fetch()
    }

    // where nickname LIKE %a%
    override fun findQByNicknameContaining(nickname: String): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.nickname.contains(nickname)
            )
            .fetch()
    }

    override fun countQByNicknameContaining(nickname: String): Long {
        val member = QMember.member

        return jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(
                member.nickname.contains(nickname)
            )
            .fetchOne() ?: 0L
    }

    override fun existsQByNicknameContaining(nickname: String): Boolean {
        val member = QMember.member

        return jpaQueryFactory
            .selectOne()
            .from(member)
            .where(
                member.nickname.contains(nickname)
            )
            .fetchFirst() != null
    }

    // 두 차례 쿼리 실행
    override fun findQByNicknameContaining(nickname: String, pageable: Pageable): Page<Member> {
        // pageable 객체 안 page, size에 대한 값은 컨트롤러에서 들어옴
        val member = QMember.member

        // content 쿼리 - 데이터 조회
        // 현재 페이지에 보여줄 데이터 목록을 가져옴
        val result = jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .offset(pageable.offset) // 앞에서 몇 개 건너뛸지 (페이지 시작 위치)
            .limit(pageable.pageSize.toLong()) // 한 페이지에 몇 개 가져올지
            .fetch() // 여러 개의 결과를 리스트로 가져오는 메서드

        // totalCount 쿼리 - 전체 개수
        // 전체 데이터의 개수를 가져옴
        val totalCount = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(member.nickname.contains(nickname))
            .fetchOne() ?: 0L // 결과 1개 반환

        // 페이지 객체 생성
        return PageImpl(
            result, // 첫 번째 쿼리 결과, 현재 페이지 데이터 목록
            pageable, // 매개변수로 받은 페이지 요청 정보 - 페이지 번호, 사이즈
            totalCount // 두 번째 쿼리 결과, 전체 데이터 개수
        )
    }
}