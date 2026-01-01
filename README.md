[boilerplate]
나만의 boilerplate

[Docker]
스프링에 Dockerfile을 두고 멀티스테이지빌드를 했음. 매번 bootjar 하지 않고, docker-compose를 통해서 원큐에 
루트 디렉토리에 docker-compose.yml을 구성했음.


[실행 방법]
docker-compose up -d


[환경 변수 관리 전략]
.env-dev와 .env-prod로 나누어서 절대로 밖으로 노출이 되면 안되는 데이터는 .env-prod에 두며
해당 파일은 .gitignore로 제외 시켰음.
.env-dev는 개발 편의를 위해 .gitignore하지 않았음.
환경 변수를 주입하는 여러 방법을 생각해 보았으나, 직접 주입하는 것이 제일 깔끔하다고 느껴졌음.
하지만, 그 과정이 번거로우니 docker-compose를 통해서 주입하기로 함.
docker-compsoe.yml에 환경변수를 설정해둠.
기본으로 .env-dev를 읽고, .env-prod에 입력된 환경 변수를 덮어씌우는 방식으로 주입함.




[설정 파일 관리 전략]
application.yml, application-dev.yml, application-prod.yml을 두어서 공통 환경, 개발 환경, 배포 환경에 대한 설정을 최적화함.
개발 환경일 때: application.yml -> application-dev.yml
운영 환경일 때: application.yml -> application-prod.yml
이 설정 파일이 선택되는 원리는 다음과 같음



[디렉토리 구조]
추후 다른 서버를 추가할 수 있는 점을 고려해, 루트 디렉토리에서 application을 두어 진행함.
application은 스프링 부트

[도메인형]
domain, common, global 패키지로 구성을 했음

domain 패키지
domain별로 패키지를 구성해서 확장성을 챙김
domain/auth
domain/email
domain/home
domain/user
추후 도메인 확장 예정

common 패키지
페이징 추가 예정
baseentitiy

global 패키지
global/security 패키지에 보안 관련.. 현재 Spring Security를 사용하지 않고, jwt로 구현을 했고, interceptor를 통해서 header에서 token을 추출해서 인증하는 방식임(현재 인증은 JWT와 클라이언트단의 로컬 스토리지를 활용해서 구현을 했음)
global/config 패키지에 설정 관련.. WebMvcConfig에서 authInterceptor 설정을 해주고 있는데, 인증 제외를 할려면 여기 추가해주어야 함
global/exception 패키지에 예외 처리 관련.. ErrorCode를 구성하고, GlobalExceptionHandler에서 Controller에서 던져준 예외를 잡아서 처리함
global/dto 패키지에 ApiResponseDto를 두어 API 표준 응답을 만듦. apiresponsedto는 success, code, message, data를 포함한다. 일반 controller에서는 apiresponsedto를 그대로 반환하고, globalexceptionhandler에서는 responseentity에 apiresponsedto를 감싼 형태로 반환한다. -> soft 200등 문제를 잡기 위해서(상태코드가 항상 200으로 고정되는 문제), Controller 단에서는 예외를 직접 던지는 것을 지양하고, 비즈니스 로직에서 로깅과 함께 예외를 던진다(비즈니스 로직에만 집중하는 것이 좋고, 로깅을 분리하는 방법이 좋다고 생각하지만, 로깅과 비즈니스 로직이 하나라고 생각함). Controller에서는 apiresponsedto의 형태로만 반환해 데이터 형태의 일관성을 가져간다.
ApiResponseDto의 형태 public class ApiResponseDto<T> {

    private final boolean success;
    private final String code;  // "200", "404" 등(추후 확장 가능성을 고려해, String 타입으로 지정)
    private final String message;
    private final T data;



추후 로깅 관련 추가진행 예정





[예쁜 코딩]
파라미터에 dto 변수명을 loginRequestDto 이런식으로 나타내는게 좋을까 -> LoginRequestDto request로 나타내자.

controller에서 repository 직접 접근하지 않도록 한다. -> 비즈니스 로직을 통해서만 접근

dto에서 setter 사용 권장하지 않음. entity도

entity에 비즈니스 메소드를 두어서 setter 역할을 하도록 함

dto에 from을 두는 것을 권장/ 근데 형태와 로직에 따라서 적합한 경우와 그렇지 않은 경우가 있다고 생각함.

controller에서 try-catch로 예외처리 하는거 좋지 못하다고 생각함(깔끔하지 않음)
비즈니스 로직에는 로그를 담는게 좋음
비즈니스 로직에서 예외를 throw

데이터가 하나인 경우에도 DTO에 담아서 받는 것이 맞다











