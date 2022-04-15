# spring-batch 수행 방법

### 1. 실행 방법 
   + 로컬 PC에 MariaDB를 설치 및 계정을 생성한다
   + MariaDB 스크립트를 실행시켜 데이터베이스 및 테이블을 생성한다(스크립트 : table_script_mariadb.sql)
   + 스프링부트 소스를 실행시킨다.
   + 배치 시작/종료, 쿼츠 스케쥴 등록/삭제 등의 기능을 API를 날려 수행한다(POSTMAN 혹은 curl 활용)


### 2. API 설명
  + 스프링 배치 JOB 시작 : GET localhost:8080/batch/start?jobName={jobName}
  + 스프링 배치 JOB 종료 : GET localhost:8080/batch/stop?jobExecutionId={jobExecutionId}
  + 쿼츠 스케쥴 등록 : POST localhost:8080/schedule
  + 쿼츠 스케쥴 수정 : PUT localhost:8080/schedule?jobName={jobName}&jobGroup={jobGroup}
  + 쿼츠 스케쥴 삭제 : DELETE localhost:8080/schedule?jobName={jobName}&jobGroup={jobGroup}
  + 쿼츠 스케쥴 즉시 시작 : GET localhost:8080/schedule/start?jobName={jobName}&jobGroup={jobGroup}
  + 쿼츠 스케쥴 중지 : GET localhost:8080/schedule/stop?jobName={jobName}&jobGroup={jobGroup}
  + 쿼츠 스케쥴 중지해제 : GET localhost:8080/schedule/resume?jobName={jobName}&jobGroup={jobGroup}


### 3. 배치 설명
+ SampleBatchJob : 입력받은 args를 로그로 출력
+ SampleDataSyncJob : first_db의 SYNC_SOURCE_TABLE의 데이터를 second_db의 SYNC_TARGET_TABLE로 동기화 후 결과를 업데이트 치는 배치(1건 당 1-commit)
+ SampleDataSyncByBulkJob : first_db의 SYNC_SOURCE_TABLE의 데이터를 second_db의 SYNC_TARGET_TABLE로 동기화 후 결과를 업데이트 치는 배치(chunk size만큼 commit)
+ SampleInterfaceJob : 외부 사이트 API 호출하는 배치(RestTemplate 활용)
+ SampleCsvFileJob :  사용 가능 여부 확인 불가(현행화 안함)
+ SampleExcelFileJob :  사용 가능 여부 확인 불가(현행화 안함)
