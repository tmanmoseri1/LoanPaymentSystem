# API Examples (curl)

## Register
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","username":"john","password":"pass123"}'
```

## Login (get token)
```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}' | jq -r .token)

echo $TOKEN
```

## Create Loan
```bash
curl -X POST http://localhost:8081/api/loans \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount":1000.00,"termMonths":12}'
```

## Pay Loan
```bash
curl -X POST http://localhost:8081/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"loanId":"<UUID>","amount":100.00}'
```
