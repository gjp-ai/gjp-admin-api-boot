import json, os

path = os.path.join(os.path.dirname(__file__), 'postman', 'GJP-API-Admin.postman_collection.json')

with open(path, 'r') as f:
    data = json.load(f)

# 1. Add verificationToken variable if not present
var_names = [v['key'] for v in data['variable']]
if 'verificationToken' not in var_names:
    data['variable'].append({
        "key": "verificationToken",
        "value": "",
        "type": "string"
    })

# 2. Define the new Email Verification folder
email_verification_folder = {
    "name": "Email Verification",
    "item": [
        {
            "name": "Verify Email",
            "request": {
                "auth": { "type": "noauth" },
                "method": "POST",
                "header": [
                    { "key": "Content-Type", "value": "application/json" }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"token\": \"{{verificationToken}}\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/v1/auth/email/verify",
                    "host": ["{{baseUrl}}"],
                    "path": ["v1", "auth", "email", "verify"]
                }
            }
        },
        {
            "name": "Resend Verification Email",
            "request": {
                "auth": { "type": "noauth" },
                "method": "POST",
                "header": [
                    { "key": "Content-Type", "value": "application/json" }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"email\": \"testuser@example.com\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/v1/auth/email/resend-verification",
                    "host": ["{{baseUrl}}"],
                    "path": ["v1", "auth", "email", "resend-verification"]
                }
            }
        }
    ]
}

# 3. Define the new Password Reset folder
password_reset_folder = {
    "name": "Password Reset",
    "item": [
        {
            "name": "Forgot Password",
            "request": {
                "auth": { "type": "noauth" },
                "method": "POST",
                "header": [
                    { "key": "Content-Type", "value": "application/json" }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"email\": \"testuser@example.com\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/v1/auth/password/forgot",
                    "host": ["{{baseUrl}}"],
                    "path": ["v1", "auth", "password", "forgot"]
                }
            }
        },
        {
            "name": "Reset Password",
            "request": {
                "auth": { "type": "noauth" },
                "method": "POST",
                "header": [
                    { "key": "Content-Type", "value": "application/json" }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"token\": \"{{verificationToken}}\",\n    \"newPassword\": \"NewPass@123\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/v1/auth/password/reset",
                    "host": ["{{baseUrl}}"],
                    "path": ["v1", "auth", "password", "reset"]
                }
            }
        }
    ]
}

# 4. Find the Auth folder and insert after Register
auth_folder = None
for item in data['item']:
    if item.get('name') == 'Auth':
        auth_folder = item
        break

if auth_folder:
    register_idx = None
    for i, sub in enumerate(auth_folder['item']):
        if sub.get('name') == 'Register':
            register_idx = i
            break
    
    if register_idx is not None:
        auth_folder['item'].insert(register_idx + 1, email_verification_folder)
        auth_folder['item'].insert(register_idx + 2, password_reset_folder)
        print(f"Inserted Email Verification and Password Reset folders after Register (index {register_idx})")
    else:
        print("ERROR: Register folder not found")

    # 5. Remove "Delete User (permanent)" from Users folder
    for sub in auth_folder['item']:
        if sub.get('name') == 'Users':
            before = len(sub['item'])
            sub['item'] = [req for req in sub['item'] if req.get('name') != 'Delete User (permanent)']
            after = len(sub['item'])
            if before != after:
                print(f"Removed 'Delete User (permanent)' from Users ({before} -> {after} items)")
            break
else:
    print("ERROR: Auth folder not found")

with open(path, 'w') as f:
    json.dump(data, f, indent='\t', ensure_ascii=False)
    f.write('\n')

print("Done. Postman collection updated.")
