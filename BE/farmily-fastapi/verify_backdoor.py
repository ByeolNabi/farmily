import requests
import jwt
import datetime

# Configuration
BASE_URL = "http://i14d101.p.ssafy.io:8081/api/v1/auth/backdoor"
SECRET_KEY = "your-super-secret-key-change-in-production" 

def test_backdoor():
    print(f"Testing {BASE_URL}...")
    try:
        response = requests.post(BASE_URL, json={"user_id": 12345})
        response.raise_for_status()
        data = response.json()
        token = data.get("access_token")
        print(f"Token received: {token[:20]}...")
        
        # Decode without verifying signature just to check payload
        decoded = jwt.decode(token, options={"verify_signature": False})
        print(f"Decoded payload: {decoded}")
        
        exp = decoded.get("exp")
        user_id = decoded.get("user_id")
        
        if user_id != 12345:
            print("FAILED: user_id mismatch")
            return
            
        # Check expiration is roughly 1 year fram now
        exp_date = datetime.datetime.fromtimestamp(exp, tz=datetime.timezone.utc)
        print(f"Expiration date: {exp_date}")
        
        now = datetime.datetime.now(datetime.timezone.utc)
        days_diff = (exp_date - now).days
        print(f"Days until expiration: {days_diff}")
        
        if 360 <= days_diff <= 370:
             print("SUCCESS: Expiration is approx 1 year.")
        else:
             print(f"FAILED: Expiration days {days_diff} is not ~365.")

    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    test_backdoor()
