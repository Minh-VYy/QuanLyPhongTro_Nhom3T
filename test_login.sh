#!/bin/bash
# Test login API endpoint

echo "Testing API login..."
curl -v -X POST "http://18.140.64.80:5000/api/nguoidung/login" \
  -H "Content-Type: application/json" \
  -d '{"Email":"admin@trotot.com","Password":"password"}'

echo ""
echo "Done!"

