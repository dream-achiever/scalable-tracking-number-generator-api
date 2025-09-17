#!/bin/bash

# Test script for Tracking Number Generator API
# This script tests all the requirements specified in the prompt

set -e

API_BASE_URL=${API_BASE_URL:-"http://localhost:8080/api/v1"}
echo "🧪 Testing API at: $API_BASE_URL"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Function to run a test
run_test() {
    local test_name="$1"
    local expected_status="$2"
    local url="$3"
    
    echo -n "Testing $test_name... "
    
    response=$(curl -s -w "\n%{http_code}" "$url" || echo "000")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✅ PASSED${NC}"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}❌ FAILED${NC} (Expected: $expected_status, Got: $http_code)"
        echo "Response: $body"
        ((TESTS_FAILED++))
    fi
}

# Function to test tracking number generation
test_tracking_generation() {
    local test_name="$1"
    local params="$2"
    local expected_status="$3"
    
    echo -n "Testing $test_name... "
    
    response=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/next-tracking-number?$params" || echo "000")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "$expected_status" ]; then
        if [ "$expected_status" = "200" ]; then
            # Check if response contains required fields
            if echo "$body" | grep -q '"tracking_number"' && echo "$body" | grep -q '"created_at"'; then
                # Check if tracking number matches pattern ^[A-Z0-9]{1,16}$
                tracking_number=$(echo "$body" | grep -o '"tracking_number":"[^"]*"' | cut -d'"' -f4)
                if echo "$tracking_number" | grep -qE '^[A-Z0-9]{1,16}$'; then
                    echo -e "${GREEN}✅ PASSED${NC}"
                    ((TESTS_PASSED++))
                else
                    echo -e "${RED}❌ FAILED${NC} (Invalid tracking number format: $tracking_number)"
                    ((TESTS_FAILED++))
                fi
            else
                echo -e "${RED}❌ FAILED${NC} (Missing required fields in response)"
                ((TESTS_FAILED++))
            fi
        else
            echo -e "${GREEN}✅ PASSED${NC}"
            ((TESTS_PASSED++))
        fi
    else
        echo -e "${RED}❌ FAILED${NC} (Expected: $expected_status, Got: $http_code)"
        echo "Response: $body"
        ((TESTS_FAILED++))
    fi
}

echo "🚀 Starting API tests..."
echo "================================"

# Test 1: Health check
run_test "Health Check" "200" "$API_BASE_URL/next-tracking-number/health"

# Test 2: Valid tracking number generation
test_tracking_generation "Valid Request" \
    "origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32Z&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics" \
    "200"

# Test 3: Another valid request to test uniqueness
test_tracking_generation "Another Valid Request" \
    "origin_country_id=US&destination_country_id=CA&weight=2.567&created_at=2024-01-15T10:30:00Z&customer_id=123e4567-e89b-12d3-a456-426614174000&customer_name=Test%20Customer&customer_slug=test-customer" \
    "200"

# Test 4: Invalid country code
test_tracking_generation "Invalid Country Code" \
    "origin_country_id=INVALID&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32Z&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics" \
    "400"

# Test 5: Missing required parameter
test_tracking_generation "Missing Weight Parameter" \
    "origin_country_id=MY&destination_country_id=ID&created_at=2018-11-20T19:29:32Z&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics" \
    "400"

# Test 6: Invalid weight (too high)
test_tracking_generation "Invalid Weight (Too High)" \
    "origin_country_id=MY&destination_country_id=ID&weight=1000.000&created_at=2018-11-20T19:29:32Z&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics" \
    "400"

# Test 7: Invalid customer slug format
test_tracking_generation "Invalid Customer Slug" \
    "origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32Z&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=Invalid_Slug" \
    "400"

# Test 8: Metrics endpoint
run_test "Metrics Endpoint" "200" "$API_BASE_URL/actuator/metrics"

# Test 9: Prometheus metrics
run_test "Prometheus Metrics" "200" "$API_BASE_URL/actuator/prometheus"

echo "================================"
echo "📊 Test Results:"
echo "✅ Tests Passed: $TESTS_PASSED"
echo "❌ Tests Failed: $TESTS_FAILED"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}💥 Some tests failed!${NC}"
    exit 1
fi
