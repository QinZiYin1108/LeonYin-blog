<template>
  <div class="social-contact">
    <div class="social-icons">
      <div class="icon-item" @mouseenter="showQrCode('wechat')" @mouseleave="hideQrCode('wechat')">
        <div class="icon wechat-icon">
          <!-- 微信图标 -->
          <img src="@/assets/wechat-icon.svg" alt="微信" class="social-icon-img" />
        </div>
        <div class="qrcode-popup" :class="{ 'show': activeQrCode === 'wechat' }">
          <div class="qrcode-container">
            <img src="@/assets/wechat-code.jpg" alt="微信二维码" class="qrcode-img" />
            <div class="qrcode-title">扫码添加微信</div>
          </div>
        </div>
      </div>
      <div class="icon-item" @mouseenter="showQrCode('qq')" @mouseleave="hideQrCode('qq')">
        <div class="icon qq-icon">
          <!-- QQ图标 -->
          <img src="@/assets/qq-icon.svg" alt="QQ" class="social-icon-img" />
        </div>
        <div class="qrcode-popup" :class="{ 'show': activeQrCode === 'qq' }">
          <div class="qrcode-container">
            <img src="@/assets/qq-code.jpg" alt="QQ二维码" class="qrcode-img" />
            <div class="qrcode-title">扫码添加QQ</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SocialContact',
  data() {
    return {
      activeQrCode: null
    }
  },
  methods: {
    showQrCode(type) {
      this.activeQrCode = type
    },
    hideQrCode(type) {
      if (this.activeQrCode === type) {
        this.activeQrCode = null
      }
    }
  }
}
</script>

<style scoped>
.social-contact {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 999;
}

.social-icons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.icon-item {
  position: relative;
  cursor: pointer;
}

.icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  transition: all 0.3s ease;
}

.wechat-icon {
  background-color: #07C160;
}

.qq-icon {
  background-color: #12B7F5;
}

.icon:hover {
  transform: scale(1.1);
}

.qrcode-popup {
  position: absolute;
  right: 50px;
  top: 0;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 10px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  transform: translateX(10px);
}

.qrcode-popup.show {
  opacity: 1;
  visibility: visible;
  transform: translateX(0);
}

.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qrcode-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  margin-bottom: 8px;
  border-radius: 4px;
}

.social-icon-img {
  width: 24px;
  height: 24px;
}

.qrcode-title {
  font-size: 12px;
  color: #333;
}
</style>