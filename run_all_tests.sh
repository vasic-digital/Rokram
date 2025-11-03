#!/bin/bash

echo "=== Yole Comprehensive Test Suite ==="
echo "Running all tests with 100% success requirement"
echo

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

run_gradle_test() {
    local module=$1
    local test_type=$2

    echo -e "${YELLOW}Running $test_type tests for $module...${NC}"

    if ./gradlew :$module:test$test_type 2>/dev/null; then
        echo -e "${GREEN}✓ $module $test_type tests PASSED${NC}"
        ((PASSED_TESTS++))
    else
        echo -e "${RED}✗ $module $test_type tests FAILED${NC}"
        ((FAILED_TESTS++))
    fi
    ((TOTAL_TESTS++))
}

# Run unit tests for all modules
MODULES=(
    "commons"
    "core"
    "format-markdown"
    "format-todotxt"
    "format-csv"
    "format-wikitext"
    "format-keyvalue"
    "format-asciidoc"
    "format-orgmode"
    "format-plaintext"
    "format-latex"
    "format-restructuredtext"
    "format-taskpaper"
    "format-textile"
    "format-creole"
    "format-tiddlywiki"
    "format-jupyter"
    "format-rmarkdown"
)

echo "=== Running Unit Tests ==="
for module in "${MODULES[@]}"; do
    run_gradle_test "$module" ""
done

echo
echo "=== Running Integration Tests ==="
# Integration tests (if any)
for module in "${MODULES[@]}"; do
    run_gradle_test "$module" "Integration"
done

echo
echo "=== Running Android Tests ==="
for module in "${MODULES[@]}"; do
    run_gradle_test "$module" "DebugUnit"
done

echo
echo "=== Test Results Summary ==="
echo "Total test suites: $TOTAL_TESTS"
echo "Passed: $PASSED_TESTS"
echo "Failed: $FAILED_TESTS"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 ALL TESTS PASSED! (100% success rate)${NC}"
    exit 0
else
    echo -e "${RED}❌ Some tests failed. Success rate: $((PASSED_TESTS * 100 / TOTAL_TESTS))%${NC}"
    exit 1
fi