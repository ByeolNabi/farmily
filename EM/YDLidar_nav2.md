# ROS 2 내비게이션 가이드: 맵 저장부터 경로 탐색 알고리즘까지

**작성일:** 2026-01-29  
**환경:** ROS 2 Humble / Jetson Orin Nano / Ubuntu 22.04  
**주제:** Nav2 맵 저장, 다익스트라 vs A\* 알고리즘, 장애물 회피 원리

---

## 1. 맵 저장하기 (Map Saver)

SLAM(예: `slam_toolbox`)을 통해 지도를 생성했다면, 나중에 내비게이션(Nav2)에서 불러올 수 있도록 파일로 저장해야 한다.

### 1.1 필수 패키지 설치

`nav2_map_server` 패키지가 설치되어 있는지 확인한다.

```bash
sudo apt install ros-humble-nav2-map-server
```

### 1.2 맵 저장 명령어

SLAM이 실행 중이고 RViz2에 지도가 보이는 상태에서, 새 터미널을 열고 다음 명령어를 실행한다. 맵 파일을 관리할 전용 폴더를 만드는 것이 좋다.

```Bash
# 1. 맵 저장용 디렉토리 생성 및 이동
mkdir -p ~/maps
cd ~/maps

# 2. 맵 저장 CLI 실행
# 사용법: ros2 run nav2_map_server map_saver_cli -f [확장자를_제외한_파일이름]
ros2 run nav2_map_server map_saver_cli -f my_map
```

### 1.3 생성된 파일 확인

명령어를 실행하면 두 개의 파일이 생성된다.

- `my_map.pgm`: 지도의 시각적 이미지 파일.
  - 흰색 (255): 이동 가능 구역 (Free space)

  - 검은색 (0): 벽/장애물 (Occupied space)

  - 회색 (205): 미탐사 구역 (Unknown space)

- `my_map.yaml`: 지도의 메타데이터 파일.
  - `resolution`: 픽셀 하나당 실제 거리 (예: 0.05는 1픽셀이 5cm)

  - `origin`: 맵 원점의 [x, y, yaw] 좌표

---

## 2. 내비게이션 실행 (Nav2)

저장된 맵을 사용하여 자율주행(Nav2)을 시작한다.

### 2.1 Nav2 런치 실행

`nav2_bringup` 패키지에서 제공하는 기본 런치 파일을 사용하며, 저장해둔 맵 파일을 지정한다.

```Bash
ros2 launch nav2_bringup bringup_launch.py map:=/home/$USER/maps/my_map.yaml
```

### 2.2 초기 위치 추정 (Localization)

Nav2가 켜지면 로봇은 자신이 지도상 어디에 있는지 모른다. AMCL(Adaptive Monte Carlo Localization)에게 초기 위치를 알려줘야 한다.

1. RViz2를 연다.

2. 상단 툴바의 [2D Pose Estimate] 버튼을 클릭한다.

3. 지도상에서 로봇의 실제 위치와 바라보는 방향에 맞춰 클릭&드래그한다.

4. 확인: 빨간색 점(라이다 스캔)이 지도의 검은색 선(벽)과 일치하는지 확인한다.

### 2.3 목표 지점 설정 (Navigation)

1. 상단 툴바의 [Nav2 Goal] 버튼을 클릭한다.

2. 가고 싶은 목적지를 클릭하고 방향을 드래그한다.

3. 로봇이 경로를 계산하고 이동을 시작한다.

---

## 3. 경로 탐색 알고리즘 (Global Planner)

목표 지점을 찍었을 때, 시작점에서 도착점까지의 큰 경로를 계산하는 것을 **전역 경로 계획(Global Planner)**이라고 한다. 대표적으로 \**다익스트라(Dijkstra)*와 A(에이스타) 알고리즘이 있다.

### 3.1 다익스트라 알고리즘 (Dijkstra's Algorithm)

- 개념: 연못에 돌을 던지면 물결이 퍼져나가듯, 시작점에서 모든 방향으로 균일하게 탐색하며 목표를 찾는다.

- 원리: 방문하지 않은 노드 중 가장 비용(거리)이 적은 노드를 선택하며 확장해 나간다.

- 장점: 반드시 **최단 경로(Shortest Path)**를 찾아낸다. 수학적으로 완벽하다.

- 단점: 목표 지점의 방향을 모르고 모든 방향(원형)을 뒤지기 때문에, 연산량이 많고 속도가 느리다.

### 3.2 A\* 알고리즘 (A-Star Algorithm)

- 개념: 다익스트라에 **"휴리스틱(Heuristic, 추정값)"**을 더해 목표 방향으로 탐색을 유도하는 방식이다.

- 공식: f(n)=g(n)+h(n)
  - g(n): 시작점에서 현재 노드까지의 실제 비용 (다익스트라와 동일)

  - h(n): 현재 노드에서 목표점까지의 예상 거리 (휴리스틱, 보통 직선 거리)

- 장점: 목표 쪽으로 먼저 탐색하므로 다익스트라보다 훨씬 빠르다.

- 단점: 휴리스틱 설정에 따라 최적의 해가 아닐 수도 있지만, 격자 지도(Grid Map)에서는 대부분 최적해를 찾는다. (ROS 2 기본값)

### 3.3 ROS 2 Nav2 적용 방법

Nav2에서는 `nav2_params.yaml` 파일의 `planner_server` 항목에서 설정할 수 있다.

설정 예시: `src/패키지명/params/nav2_params.yaml` 파일을 열어 확인한다.

```YAML
planner_server:
  ros__parameters:
    planner_plugins: ["GridBased"]
    use_sim_time: True

    GridBased:
      plugin: "nav2_navfn_planner/NavFnPlanner"
      tolerance: 0.5
      use_astar: true   # <--- True면 A* 사용, False면 다익스트라 사용
      allow_unknown: true
```

---

## 4. 장애물 회피 알고리즘 (Local Planner / Controller)

Global Planner가 지도를 보고 큰 길을 짰다면, **지역 경로 계획(Local Planner/Controller)**은 실제로 로봇을 움직이며 갑자기 나타난 장애물(사람, 강아지 등)을 피하는 역할을 한다.

### 4.1 핵심 개념: 비용 지도 (Costmap)

로봇은 세상을 "비용(Cost)"으로 본다.

- Lethal Obstacle (254): 벽이나 장애물의 중심. 부딪히면 끝장.

- Inflation Radius (팽창 반경): 장애물 주변의 완충 지대. 로봇이 벽에 너무 붙지 않도록 비용을 부풀려 놓은 영역.

### 4.2 알고리즘: DWB (Dynamic Window Approach)

ROS 2 Humble의 기본 컨트롤러다.

1. 샘플링 (Sampling): 로봇이 현재 속도에서 짧은 시간(예: 1.5초) 동안 갈 수 있는 여러 개의 가상 경로(부채꼴 모양)를 만든다.

2. 점수 매기기 (Scoring): 각 경로에 점수를 매긴다.
   - PathAlign: 전역 경로(Global Path)와 가까운가?

   - GoalAlign: 목표 지점을 향하고 있는가?

   - BaseObstacle: 경로상에 장애물(Costmap의 높은 값)이 있는가? (있으면 0점 처리)

3. 선택 (Selection): 점수가 가장 높은 경로를 선택해 모터를 구동한다.

### 4.3 장애물 회피 적용 설정

`nav2_params.yaml`의 `controller_server` 부분에서 설정한다.

```YAML
controller_server:
  ros__parameters:
    controller_plugins: ["FollowPath"]

    FollowPath:
      plugin: "dwb_core::DWBLocalPlanner"
      min_vel_x: 0.0
      max_vel_x: 0.26

      # Critics(채점관)들이 로봇의 행동을 결정함
      critics: ["RotateToGoal", "Oscillation", "BaseObstacle", "GoalAlign", "PathAlign"]

      # 장애물 회피 가중치 (높을수록 장애물을 더 무서워해서 크게 돎)
      BaseObstacle.scale: 0.02

      # 전역 경로 추종 가중치 (높을수록 경로를 엄격하게 따름)
      PathAlign.scale: 32.0
```

### 4.4 작동 흐름 요약

1. 센서 감지: 라이다가 갑자기 나타난 장애물을 본다.

2. Costmap 업데이트: 로봇 앞의 빈 공간이었던 곳이 '위험 구역(높은 Cost)'으로 바뀐다.

3. DWB 계산: 직진하는 경로를 시뮬레이션해 보니 위험 구역과 겹쳐서 점수가 낮아진다. 반면 옆으로 비켜가는 경로는 점수가 높다.

4. 회피 주행: 로봇이 점수가 높은 옆쪽 경로를 선택해 장애물을 피해 간다.
