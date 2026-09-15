#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import json, sys, urllib.request

TOKEN = open("/sdcard/Download/webwrap/token.txt").read().strip()
OWNER, REPO = "xiaoguangdaw", "xiaoguangapp"

def api(url):
    req = urllib.request.Request(url)
    req.add_header("Authorization", "token " + TOKEN)
    req.add_header("Accept", "application/vnd.github+json")
    req.add_header("User-Agent", "insp")
    with urllib.request.urlopen(req) as r:
        return json.loads(r.read().decode() or "{}")

runs = api(f"https://api.github.com/repos/{OWNER}/{REPO}/actions/runs?per_page=10")["workflow_runs"]
print("=== 最近运行 ===")
for x in runs:
    print(f'  run {x["run_number"]}  id={x["id"]}  {x["status"]}/{x["conclusion"]}  event={x["event"]}  created={x["created_at"]}')

target = int(sys.argv[1]) if len(sys.argv) > 1 else runs[-1]["run_number"]
rid = [x["id"] for x in runs if x["run_number"] == target][0]
print(f"\n=== run {target} (id={rid}) 的 jobs & steps ===")
jobs = api(f"https://api.github.com/repos/{OWNER}/{REPO}/actions/runs/{rid}/jobs")
for j in jobs["jobs"]:
    print(f'JOB: {j["name"]}  -> {j["conclusion"]}')
    for s in j["steps"]:
        print(f'   [{s["conclusion"]}] {s["number"]}. {s["name"]}')
