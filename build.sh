#!/bin/sh
# ============================================================
#  一键打包脚本（给不想学 GitHub 的人，用 Termux 跑）
#  前提：手机已装 Termux，且已安装 openjdk-17 + android-sdk
#  （小白建议直接用 GitHub Actions，不要用这个）
# ============================================================
set -e

APP_TITLE="${1:-我的应用}"
STAMP=$(date +%y%m%d%H%M)
RND=$(printf '%04x' $((RANDOM % 65536)))
APP_ID="com.mytools.a${STAMP}${RND}"

echo "==================================="
echo " 应用名: $APP_TITLE"
echo " 包名  : $APP_ID"
echo "==================================="

./gradlew assembleRelease -PappTitle="$APP_TITLE" -PappId="$APP_ID"

echo ""
echo "完成！APK 在：app/build/outputs/apk/release/"
ls -la app/build/outputs/apk/release/