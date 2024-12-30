<template>
  <div class="qr-scan">
    <h1>QR Code Scanner</h1>
    
    <div class="scan-section">
      <qrcode-stream @decode="onDecode" @init="onInit"></qrcode-stream>
    </div>

    <div class="manual-input">
      <h3>또는 시리얼 번호 직접 입력</h3>
      <div class="form-group">
        <input v-model="serialNumber" 
               type="text" 
               placeholder="시리얼 번호를 입력하세요"
               class="form-control">
        <button @click="verifySerial" class="verify-btn">확인</button>
      </div>
    </div>

    <div v-if="error" class="error">
      {{ error }}
    </div>

    <!-- 스캔 결과 표시 -->
    <div v-if="result" class="result">
      <h2>Scan Result</h2>
      
      <!-- Base Layer (공개 정보) -->
      <div class="info-section">
        <h3>Product Information</h3>
        <p><strong>Product ID:</strong> {{ result.productId }}</p>
        <p><strong>Product Name:</strong> {{ result.productName }}</p>
        <p><strong>Category:</strong> {{ result.productCategory }}</p>
        <p><strong>Manufacturer:</strong> {{ result.manufacturer }}</p>
      </div>

      <!-- Auth Layer (인증 정보) -->
      <div class="info-section">
        <h3>Authentication Status</h3>
        <p><strong>Serial Number:</strong> {{ result.serialNumber }}</p>
        <p><strong>Status:</strong> 
          <span :class="result.authenticated ? 'authentic' : 'not-authentic'">
            {{ result.authenticated ? 'Authentic' : 'Not Verified' }}
          </span>
        </p>
      </div>

      <!-- 이력 정보 -->
      <div class="info-section">
        <h3>History</h3>
        <div v-if="result.authenticationHistory?.length" class="history-list">
          <div v-for="(history, index) in result.authenticationHistory" 
               :key="index" 
               class="history-item">
            <p><strong>Date:</strong> {{ formatDate(history.timestamp) }}</p>
            <p><strong>Location:</strong> {{ history.location }}</p>
            <p><strong>Result:</strong> {{ history.result }}</p>
          </div>
        </div>
        <p v-else>No authentication history available</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { QrcodeStream } from 'vue-qrcode-reader'
import axios from 'axios'

const error = ref('')
const result = ref(null)
const serialNumber = ref('')

const onDecode = async (decodedString) => {
  try {
    const response = await axios.post('http://localhost:8081/api/qrcode/verify', {
      qrData: decodedString
    })
    result.value = response.data
  } catch (err) {
    error.value = 'Failed to verify QR code'
    console.error('Error:', err)
  }
}

const onInit = async (promise) => {
  try {
    await promise
  } catch (err) {
    if (err.name === 'NotAllowedError') {
      error.value = 'ERROR: Need camera access permission'
    } else if (err.name === 'NotFoundError') {
      error.value = 'ERROR: No camera found on device'
    } else if (err.name === 'NotSupportedError') {
      error.value = 'ERROR: Secure context required (HTTPS, localhost)'
    } else if (err.name === 'NotReadableError') {
      error.value = 'ERROR: Camera already in use'
    } else if (err.name === 'OverconstrainedError') {
      error.value = 'ERROR: Installed cameras are not suitable'
    } else if (err.name === 'StreamApiNotSupportedError') {
      error.value = 'ERROR: Stream API is not supported in this browser'
    } else {
      error.value = 'ERROR: Unknown error occurred'
    }
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

const verifySerial = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/qrcode/verify/serial', {
      serialNumber: serialNumber.value
    })
    result.value = response.data
  } catch (err) {
    error.value = '시리얼 번호 확인 실패'
    console.error('Error:', err)
  }
}
</script>

<style scoped>
.qr-scan {
  max-width: 1280px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

.scan-section {
  max-width: 500px;
  margin: 2rem auto;
}

.error {
  color: red;
  margin: 1rem 0;
}

.result {
  margin-top: 2rem;
  text-align: left;
}

.info-section {
  margin: 1.5rem 0;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.info-section h3 {
  margin-top: 0;
  color: #2c3e50;
}

.authentic {
  color: #4CAF50;
  font-weight: bold;
}

.not-authentic {
  color: #f44336;
  font-weight: bold;
}

.history-list {
  margin-top: 1rem;
}

.history-item {
  padding: 0.5rem;
  border-bottom: 1px solid #eee;
}

.history-item:last-child {
  border-bottom: none;
}

.manual-input {
  margin: 2rem auto;
  max-width: 500px;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.form-group {
  display: flex;
  gap: 1rem;
}

.form-control {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.verify-btn {
  padding: 0.5rem 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.verify-btn:hover {
  background-color: #45a049;
}
</style> 