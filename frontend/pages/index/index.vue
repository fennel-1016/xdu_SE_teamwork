<template>
  <view class="page">
    <view class="hero">
      <view>
        <text class="eyebrow">{{ loginStatusText }}</text>
        <text class="title">饭电</text>
        <text class="subtitle">不知道吃什么时，交给今天的手气。</text>
      </view>
      <button class="icon-button" :disabled="isBusy" @click="handleRelogin">↻</button>
    </view>

    <view class="wheel-section">
      <view class="pointer"></view>
      <view class="wheel" :class="{ spinning: isSpinning }" :style="{ transform: wheelTransform }">
        <view
          v-for="(item, index) in wheelItems"
          :key="item"
          class="wheel-item"
          :style="{ transform: `rotate(${index * 45}deg)` }"
        >
          <text>{{ item }}</text>
        </view>
        <view class="wheel-center">
          <text>饭电</text>
        </view>
      </view>
    </view>

    <button class="draw-button" :disabled="isBusy" @click="handleDraw">
      {{ isBusy ? '正在充能...' : '闪电抽选' }}
    </button>

    <view class="result-panel" v-if="selectedFood">
      <text class="result-label">今天吃这个</text>
      <text class="food-name">{{ selectedFood.name }}</text>
      <view class="food-meta">
        <text>{{ selectedFood.canteen || '未知食堂' }}</text>
        <text>{{ selectedFood.window || '未知窗口' }}</text>
      </view>
      <text class="price">¥ {{ formatPrice(selectedFood.price) }}</text>
    </view>

    <view class="empty-panel" v-else>
      <text>点一下按钮，转盘会替你做决定。</text>
    </view>
  </view>
</template>

<script>
import { ensureLogin, clearLogin } from '@/utils/auth'
import { getRandomFood } from '@/utils/api'

const MIN_SPIN_DURATION = 1600

export default {
  data() {
    return {
      openid: '',
      isBusy: false,
      isSpinning: false,
      selectedFood: null,
      rotation: 0,
      wheelItems: ['盖饭', '米线', '面食', '小炒', '水饺', '麻辣烫', '套餐', '轻食']
    }
  },

  computed: {
    loginStatusText() {
      return this.openid ? '已完成微信身份校验' : '正在准备微信登录'
    },

    wheelTransform() {
      return `rotate(${this.rotation}deg)`
    }
  },

  async onLoad() {
    await this.bootstrapLogin()
  },

  async onPullDownRefresh() {
    await this.bootstrapLogin(true)
    uni.stopPullDownRefresh()
  },

  methods: {
    async bootstrapLogin(force = false) {
      try {
        this.openid = await ensureLogin(force)
      } catch (error) {
        this.showToast(error.message || '登录失败，将以游客状态停留')
      }
    },

    async handleRelogin() {
      if (this.isBusy) {
        return
      }

      clearLogin()
      await this.bootstrapLogin(true)
    },

    async handleDraw() {
      if (this.isBusy) {
        return
      }

      this.isBusy = true
      this.isSpinning = true
      this.selectedFood = null
      this.rotation += 720 + Math.floor(Math.random() * 360)

      uni.showLoading({
        title: '寻找能量中...',
        mask: true
      })

      const startedAt = Date.now()

      try {
        const openid = this.openid || await ensureLogin()
        this.openid = openid
        const food = await getRandomFood(openid)
        await this.waitForMinimumSpin(startedAt)
        this.selectedFood = this.normalizeFood(food)
        this.rotation += this.resolveStopAngle(this.selectedFood.name)
        this.showToast('抽取成功')
      } catch (error) {
        await this.waitForMinimumSpin(startedAt)
        this.showToast(error.message || '食堂大厨还没开工，暂无菜品')
      } finally {
        this.isSpinning = false
        this.isBusy = false
        uni.hideLoading()
      }
    },

    waitForMinimumSpin(startedAt) {
      const rest = Math.max(0, MIN_SPIN_DURATION - (Date.now() - startedAt))

      return new Promise((resolve) => {
        setTimeout(resolve, rest)
      })
    },

    normalizeFood(food) {
      return {
        name: food && food.name ? food.name : '神秘美食',
        price: food && food.price !== undefined ? food.price : 0,
        canteen: food && food.canteen ? food.canteen : '',
        window: food && food.window ? food.window : ''
      }
    },

    resolveStopAngle(name) {
      const index = Math.abs(String(name || '').split('').reduce((sum, char) => sum + char.charCodeAt(0), 0)) % this.wheelItems.length
      return 360 - index * 45
    },

    formatPrice(price) {
      const value = Number(price)
      return Number.isFinite(value) ? value.toFixed(1) : '0.0'
    },

    showToast(title) {
      uni.showToast({
        title,
        icon: 'none',
        duration: 1800
      })
    }
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 40rpx 34rpx 56rpx;
  background: linear-gradient(180deg, #fff7ed 0%, #fff1e0 44%, #f7fbff 100%);
}

.hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24rpx;
}

.eyebrow {
  display: block;
  color: #5b6b80;
  font-size: 24rpx;
}

.title {
  display: block;
  margin-top: 12rpx;
  color: #152033;
  font-size: 72rpx;
  font-weight: 800;
  line-height: 1;
}

.subtitle {
  display: block;
  max-width: 520rpx;
  margin-top: 18rpx;
  color: #52616f;
  font-size: 30rpx;
  line-height: 1.5;
}

.icon-button {
  width: 76rpx;
  height: 76rpx;
  border: 2rpx solid rgba(21, 32, 51, 0.12);
  border-radius: 50%;
  color: #152033;
  background: rgba(255, 255, 255, 0.78);
  font-size: 36rpx;
  line-height: 76rpx;
  text-align: center;
}

.wheel-section {
  position: relative;
  display: flex;
  justify-content: center;
  margin: 70rpx 0 48rpx;
}

.pointer {
  position: absolute;
  top: -6rpx;
  left: 50%;
  z-index: 3;
  width: 0;
  height: 0;
  margin-left: -22rpx;
  border-right: 22rpx solid transparent;
  border-left: 22rpx solid transparent;
  border-top: 54rpx solid #152033;
}

.wheel {
  position: relative;
  width: 610rpx;
  height: 610rpx;
  border: 18rpx solid #ffffff;
  border-radius: 50%;
  background:
    conic-gradient(
      from -22.5deg,
      #ffcf5d 0deg 45deg,
      #f2775d 45deg 90deg,
      #6bc7b5 90deg 135deg,
      #f6a25b 135deg 180deg,
      #7ba9ff 180deg 225deg,
      #ffcf5d 225deg 270deg,
      #f2775d 270deg 315deg,
      #6bc7b5 315deg 360deg
    );
  box-shadow: 0 30rpx 70rpx rgba(151, 96, 36, 0.18);
  transition: transform 1.6s cubic-bezier(0.18, 0.84, 0.2, 1);
}

.wheel.spinning {
  animation: spin 0.42s linear infinite;
}

.wheel-item {
  position: absolute;
  top: 40rpx;
  left: 50%;
  width: 118rpx;
  height: 260rpx;
  margin-left: -59rpx;
  transform-origin: 50% 265rpx;
  color: #152033;
  font-size: 25rpx;
  font-weight: 700;
  text-align: center;
}

.wheel-center {
  position: absolute;
  top: 50%;
  left: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 170rpx;
  height: 170rpx;
  margin: -85rpx 0 0 -85rpx;
  border: 12rpx solid rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  color: #ffffff;
  background: #152033;
  font-size: 38rpx;
  font-weight: 800;
  box-shadow: 0 16rpx 34rpx rgba(21, 32, 51, 0.24);
}

.draw-button {
  width: 100%;
  height: 104rpx;
  border-radius: 52rpx;
  color: #ffffff;
  background: #152033;
  font-size: 34rpx;
  font-weight: 800;
  line-height: 104rpx;
  box-shadow: 0 18rpx 36rpx rgba(21, 32, 51, 0.2);
}

.draw-button[disabled],
.icon-button[disabled] {
  opacity: 0.58;
}

.result-panel,
.empty-panel {
  margin-top: 34rpx;
  padding: 34rpx;
  border: 2rpx solid rgba(21, 32, 51, 0.08);
  border-radius: 16rpx;
  background: rgba(255, 255, 255, 0.76);
}

.result-label {
  display: block;
  color: #e06745;
  font-size: 24rpx;
  font-weight: 700;
}

.food-name {
  display: block;
  margin-top: 12rpx;
  color: #152033;
  font-size: 48rpx;
  font-weight: 800;
  line-height: 1.2;
}

.food-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 22rpx;
  color: #52616f;
  font-size: 28rpx;
}

.price {
  display: block;
  margin-top: 22rpx;
  color: #0d7f6a;
  font-size: 36rpx;
  font-weight: 800;
}

.empty-panel {
  color: #64748b;
  font-size: 28rpx;
  line-height: 1.5;
  text-align: center;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}
</style>
