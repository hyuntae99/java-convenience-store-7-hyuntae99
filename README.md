# java-convenience-store-precourse

## 편의점

## 💻 기능 목록
### 1. **로컬 데이터 로딩**
`products.md`와 `promotions.md` 파일에서 상품 목록과 프로모션 목록을 로드합니다.

### 2. **상품 목록 출력**
현재 재고 상태와 프로모션 정보를 포함한 상품 목록을 출력합니다.
<br>재고가 없는 상품은 "재고 없음"으로 표시됩니다.

### 3. **상품 구매**
사용자가 구매할 상품명과 수량을 입력받습니다.
<br>입력 형식 검증 및 예외 처리를 수행합니다.
<br>재고 확인 후 구매 가능 여부를 판단합니다.

### 4. **프로모션 적용**
오늘 날짜를 기준으로 프로모션 기간 내인지 확인하고, 프로모션 혜택을 적용합니다.
<br>프로모션 재고 관리 및 혜택 적용 여부를 결정합니다.

### 5. **멤버십 할인 적용**
멤버십 회원 여부를 확인하고, 할인 혜택을 적용합니다.
<br>최대 할인 한도는 8,000원입니다.

### 6. **결제 금액 계산**
상품 가격, 프로모션 할인, 멤버십 할인을 반영한 최종 결제 금액을 계산합니다.

### 7. **영수증 출력**
구매 내역, 증정 상품 내역, 금액 정보를 포함한 영수증을 출력합니다.
<br>추가 구매 여부를 물어봅니다.

### 8. **추가 구매 처리**
사용자가 추가 구매를 원할 경우, 재고 상태를 업데이트하고 추가 구매를 진행합니다.
<br>구매를 종료할 경우 프로그램을 종료합니다.


## 📌 예외 사항
### <상품 구매 입력>
1. 입력이 들어오지 않을 경우
2. 입력 형식이 올바르지 않은 경우 (예: [상품명-수량] 형식 미준수)
3. 존재하지 않는 상품을 입력한 경우
4. 구매 수량이 재고 수량을 초과한 경우
5. 기타 잘못된 입력의 경우

### <프로모션 적용>
1. 프로모션 재고가 부족한 경우

### <멤버십 할인 적용>
1. 멤버십 할인 적용 여부 입력이 올바르지 않은 경우

### <결제 금액 계산>
1. 금액 계산 오류 발생 시


## ✨ 추가 고려 사항
1. 기능을 최대한 세분화한다.
2. 같은 기능을 가진 클래스끼리 묶어 패키지로 관리한다. (예: domain, service, view, utils)
3. JUnit 5와 AssertJ를 이용하여 각 기능이 정상적으로 작동하는지 클래스 별로 단위 테스트 코드를 작성한다.
4. else 사용을 지양한다. (case문 사용 지양)
5. Enum 사용을 지향한다.
6. Indent depth가 3이 넘지 않도록 구현한다.
7. 함수(메서드) 길이를 10라인 이하로 유지한다.
8. 비즈니스 로직과 UI 로직을 분리한다.