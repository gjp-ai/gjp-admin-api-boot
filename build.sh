#!/bin/bash
#
# GJP API Admin - Build Script
# ============================
# Installs dependencies and builds the gjp-admin-api-boot application.
#

set -euo pipefail

# ── Resolve project directory ─────────────────────────────────────────────────
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${PROJECT_DIR}"

# ── Parse arguments ──────────────────────────────────────────────────────────
SKIP_TESTS=true

while [[ $# -gt 0 ]]; do
    case "$1" in
        --run-tests) SKIP_TESTS=false; shift ;;
        --help)
            echo "Usage: $0 [--run-tests]"
            echo ""
            echo "Options:"
            echo "  --run-tests    Run unit tests during build (default: skip)"
            echo "  --help         Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# ── Display build info ───────────────────────────────────────────────────────
echo ""
echo "============================================"
echo "  GJP Admin API - Building"
echo "============================================"
echo ""
echo "Project Dir : ${PROJECT_DIR}"
echo "Skip Tests  : ${SKIP_TESTS}"
echo ""

# ── Build the application ─────────────────────────────────────────────────────
echo "Cleaning and building application..."
echo ""

if [[ "${SKIP_TESTS}" == true ]]; then
    ./mvnw clean install -DskipTests
else
    ./mvnw clean install
fi

echo ""
echo "============================================"
echo "  Build Completed Successfully"
echo "============================================"
echo ""
