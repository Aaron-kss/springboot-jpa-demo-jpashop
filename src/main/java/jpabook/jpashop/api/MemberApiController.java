package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("api/v2/members")
    public Result  membersV2(){
        List<Member> findMember = memberService.findMembers();
        List<MemberDto> collect = findMember.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    @PostMapping("api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }

    @PostMapping("api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){

        Member member = Member.builder().name(request.getName()).build();
        Long memberId = memberService.join(member);

        return new CreateMemberResponse(memberId);
    }

    @PutMapping("api/v2/members/{id}")
    public UpdateMemberResponse saveMemberV2(@PathVariable("id") Long memberId, @RequestBody @Valid UpdateMemberResponse request){
        memberService.update(memberId, request.getName());
        Member member = memberService.findMember(memberId);
        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    @Data
    static class CreateMemberResponse {
        public CreateMemberResponse(Long memberId) {
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
