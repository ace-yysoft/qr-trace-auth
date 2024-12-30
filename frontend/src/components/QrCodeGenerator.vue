<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8081/api'

interface QrCodeRequest {
  baseInfo: {
    productId: string
    productName: string
    productCategory: string
    manufacturer: string
  }
  authInfo: {
    authType: string
    authData: Record<string, string>
  }
  privateInfo: {
    manufacturer: {
      facilityId: string
      productionLine: string
      batchNumber: string
    }
    additionalData: Record<string, string>
  }
}

const request = ref<QrCodeRequest>({
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

const result = ref(null)

const createQrCode = async () => {
  try {
    const response = await axios.post(`${API_BASE_URL}/qrcodes`, request.value);
    result.value = response.data;
    alert('QR 코드가 성공적으로 생성되었습니다!');
  } catch (error) {
    console.error('Error creating QR code:', error);
    alert('QR 코드 생성 중 오류가 발생했습니다');
  }
}

const verifyQrCode = async (serialNumber: string) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/qrcodes/verify/serial`,
      { serialNumber }
    );
    return response.data;
  } catch (error) {
    console.error('Error verifying QR code:', error);
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 400) {
        alert('유효하지 않은 시리얼 번호입니다.');
      } else if (error.response?.status === 404) {
        alert('존재하지 않는 QR 코드입니다.');
      } else {
        alert('QR 코드 검증 중 오류가 발생했습니다.');
      }
    } else {
      alert('네트워크 오류가 발생했습니다.');
    }
    throw error;
  }
};
</script>

<template>
  <div class="qr-generator">
    <h2>QR Code Generator</h2>
    <form @submit.prevent="createQrCode">
      <!-- Base Information -->
      <div class="form-section">
        <h3>Base Information</h3>
        <div class="form-group">
          <label>Product ID:</label>
          <input v-model="request.baseInfo.productId" placeholder="예: ELEC-2024-001" />
        </div>
        <div class="form-group">
          <label>Product Name:</label>
          <input v-model="request.baseInfo.productName" placeholder="예: Smart Watch Pro" />
        </div>
        <div class="form-group">
          <label>Category:</label>
          <input v-model="request.baseInfo.productCategory" placeholder="예: Electronics" />
        </div>
        <div class="form-group">
          <label>Manufacturer:</label>
          <input v-model="request.baseInfo.manufacturer" placeholder="예: TechCorp Inc." />
        </div>
      </div>

      <!-- Authentication Information -->
      <div class="form-section">
        <h3>Authentication Information</h3>
        <div class="form-group">
          <label>Auth Type:</label>
          <select v-model="request.authInfo.authType">
            <option value="BASIC">Basic</option>
            <option value="ADVANCED">Advanced</option>
          </select>
        </div>
      </div>

      <!-- Manufacturing Information -->
      <div class="form-section">
        <h3>Manufacturing Information</h3>
        <div class="form-group">
          <label>Facility ID:</label>
          <input v-model="request.privateInfo.manufacturer.facilityId" placeholder="예: TECH-FAC-001" />
        </div>
        <div class="form-group">
          <label>Production Line:</label>
          <input v-model="request.privateInfo.manufacturer.productionLine" placeholder="예: WATCH-LINE-A" />
        </div>
        <div class="form-group">
          <label>Batch Number:</label>
          <input v-model="request.privateInfo.manufacturer.batchNumber" placeholder="예: SW24-001-BATCH" />
        </div>
      </div>

      <button type="submit" class="submit-button">Generate QR Code</button>
    </form>

    <!-- Result Display -->
    <div v-if="result" class="result-section">
      <h3>Generated QR Code</h3>
      <div class="result-info">
        <p><strong>Serial Number:</strong> {{ result.serialNumber }}</p>
        <p><strong>Product ID:</strong> {{ result.productId }}</p>
        <p><strong>Created:</strong> {{ new Date(result.manufacturingDate).toLocaleString() }}</p>
      </div>
      <pre class="result-json">{{ JSON.stringify(result, null, 2) }}</pre>
    </div>
  </div>
</template>

<style scoped>
.qr-generator {
  max-width: 900px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

h2 {
  color: #1a202c;
  font-size: 2rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
}

h2::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: #42b983;
  border-radius: 2px;
}

h3 {
  color: #2d3748;
  font-size: 1.5rem;
  font-weight: 500;
  margin-bottom: 1.5rem;
}

.form-section {
  background: #f8fafc;
  padding: 2rem;
  border-radius: 12px;
  margin-bottom: 2rem;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.form-section:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #4a5568;
  font-weight: 500;
  font-size: 0.95rem;
}

input, select {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
}

input:hover, select:hover {
  border-color: #cbd5e0;
}

input:focus, select:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.submit-button {
  background: linear-gradient(45deg, #42b983, #3aa876);
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1.1rem;
  font-weight: 600;
  width: 100%;
  margin-top: 2rem;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.submit-button:hover {
  background: linear-gradient(45deg, #3aa876, #42b983);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.2);
}

.submit-button:active {
  transform: translateY(0);
}

.result-section {
  margin-top: 3rem;
  padding: 2rem;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  animation: fadeIn 0.5s ease-out;
}

.result-info {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.result-info p {
  margin: 0.5rem 0;
  color: #4a5568;
}

.result-info strong {
  color: #2d3748;
  margin-right: 0.5rem;
}

.result-json {
  background: #2d3748;
  color: #e2e8f0;
  padding: 1.5rem;
  border-radius: 8px;
  overflow-x: auto;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 0.9rem;
  line-height: 1.5;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .qr-generator {
    margin: 1rem;
    padding: 1rem;
  }

  .form-section {
    padding: 1.5rem;
  }
}
</style> 