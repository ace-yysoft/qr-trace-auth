<template>
  <div class="qr-code">
    <h1>QR Code Management</h1>
    
    <!-- QR 코드 생성 폼 -->
    <div class="qr-form">
      <h2>Create QR Code</h2>
      
      <!-- Base Layer Info -->
      <div class="form-section">
        <h3>Base Information</h3>
        <div class="form-group">
          <label>Product ID:</label>
          <input v-model="productData.baseInfo.productId" type="text">
        </div>
        <div class="form-group">
          <label>Product Name:</label>
          <input v-model="productData.baseInfo.productName" type="text">
        </div>
        <div class="form-group">
          <label>Category:</label>
          <input v-model="productData.baseInfo.productCategory" type="text">
        </div>
        <div class="form-group">
          <label>Manufacturer:</label>
          <input v-model="productData.baseInfo.manufacturer" type="text">
        </div>
      </div>

      <!-- Auth Layer Info -->
      <div class="form-section">
        <h3>Authentication Information</h3>
        <div class="form-group">
          <label>Auth Type:</label>
          <select v-model="productData.authInfo.authType">
            <option value="BASIC">Basic</option>
            <option value="ADVANCED">Advanced</option>
          </select>
        </div>
      </div>

      <!-- Private Layer Info -->
      <div class="form-section">
        <h3>Manufacturing Information</h3>
        <div class="form-group">
          <label>Facility ID:</label>
          <input v-model="productData.privateInfo.manufacturer.facilityId" type="text">
        </div>
        <div class="form-group">
          <label>Production Line:</label>
          <input v-model="productData.privateInfo.manufacturer.productionLine" type="text">
        </div>
        <div class="form-group">
          <label>Batch Number:</label>
          <input v-model="productData.privateInfo.manufacturer.batchNumber" type="text">
        </div>
      </div>

      <button @click="generateQrCode">Generate QR Code</button>
    </div>

    <!-- QR 코드 표시 영역 개선 -->
    <div v-if="qrCodeData" class="qr-result">
      <h2>Generated QR Code</h2>
      
      <!-- QR 코드 이미지 -->
      <div class="qr-image">
        <img :src="`data:image/png;base64,${qrCodeData}`" alt="QR Code" />
      </div>

      <!-- 시리얼 번호 표시 및 복사 -->
      <div class="serial-info">
        <h3>Serial Number</h3>
        <div class="copy-section">
          <input 
            type="text" 
            :value="serialNumber" 
            readonly 
            ref="serialInput"
            class="serial-input"
          >
          <button @click="copySerial" class="copy-btn">
            {{ copied ? 'Copied!' : 'Copy' }}
          </button>
        </div>
        <p class="help-text">Use this serial number to verify the product authenticity</p>
      </div>

      <!-- 생성 정보 요약 -->
      <div class="summary-info">
        <h3>Product Information</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">Product:</span>
            <span class="value">{{ productData.baseInfo.productName }}</span>
          </div>
          <div class="info-item">
            <span class="label">Category:</span>
            <span class="value">{{ productData.baseInfo.productCategory }}</span>
          </div>
          <div class="info-item">
            <span class="label">Manufacturer:</span>
            <span class="value">{{ productData.baseInfo.manufacturer }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import axios from 'axios'

const qrCodeData = ref('')
const serialNumber = ref('')
const copied = ref(false)
const serialInput = ref(null)

// 제품 데이터 초기화
const productData = reactive({
  baseInfo: {
    productId: '',
    productName: '',
    productCategory: '',
    manufacturer: ''
  },
  authInfo: {
    authType: 'BASIC',
    authData: {}
  },
  privateInfo: {
    manufacturer: {
      facilityId: '',
      productionLine: '',
      batchNumber: ''
    },
    additionalData: {}
  }
})

const generateQrCode = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/qrcode', productData)
    if (typeof response.data === 'string' && response.data.startsWith('Error')) {
      console.error(response.data)
      return
    }
    
    // QR 코드 이미지 데이터
    qrCodeData.value = response.data.qrImage
    // 시리얼 번호
    serialNumber.value = response.data.serialNumber
  } catch (error) {
    console.error('Error generating QR code:', error)
  }
}

// 시리얼 번호 복사 함수
const copySerial = () => {
  if (serialInput.value) {
    serialInput.value.select()
    document.execCommand('copy')
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2000)
  }
}
</script>

<style scoped>
.qr-code {
  max-width: 1280px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

.qr-form {
  margin: 2rem auto;
  max-width: 600px;
  text-align: left;
}

.form-section {
  margin: 2rem 0;
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
}

.form-section h3 {
  margin-top: 0;
  color: #2c3e50;
}

.form-group {
  margin: 1rem 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

button {
  width: 100%;
  padding: 1rem;
  font-size: 1.1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 2rem;
}

button:hover {
  background-color: #45a049;
}

.qr-result {
  margin-top: 2rem;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.qr-image {
  margin: 1rem auto;
  padding: 1rem;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  width: fit-content;
}

.qr-image img {
  max-width: 300px;
}

.serial-info {
  margin: 2rem auto;
  max-width: 500px;
}

.copy-section {
  display: flex;
  gap: 0.5rem;
  margin: 1rem 0;
}

.serial-input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: monospace;
  font-size: 1.1rem;
  background-color: #f8f9fa;
}

.copy-btn {
  padding: 0.5rem 1rem;
  background-color: #2c3e50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  min-width: 80px;
}

.copy-btn:hover {
  background-color: #34495e;
}

.help-text {
  color: #666;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.summary-info {
  margin-top: 2rem;
  padding: 1rem;
  border-top: 1px solid #ddd;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.label {
  color: #666;
  font-size: 0.9rem;
}

.value {
  font-weight: bold;
  color: #2c3e50;
}
</style> 