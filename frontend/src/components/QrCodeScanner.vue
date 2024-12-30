<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8081/api'
const serialNumber = ref('')
const scanResult = ref(null)
const loading = ref(false)

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});

const verifyQrCode = async () => {
  if (!serialNumber.value) {
    alert('시리얼 번호를 입력해주세요');
    return;
  }

  loading.value = true;
  try {
    const response = await axiosInstance.post(
      `/qrcodes/verify/serial`,
      { serialNumber: serialNumber.value }
    );
    scanResult.value = response.data;
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
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="qr-scanner">
    <h2>QR Code Scanner</h2>
    <div class="scan-section">
      <div class="form-group">
        <label>Serial Number:</label>
        <input 
          v-model="serialNumber"
          placeholder="시리얼 번호를 입력하세요"
          :disabled="loading"
        />
      </div>
      <button 
        @click="verifyQrCode" 
        class="verify-button"
        :disabled="loading"
      >
        {{ loading ? '검증 중...' : 'QR 코드 검증' }}
      </button>
    </div>

    <div v-if="scanResult" class="result-section">
      <h3>Verification Result</h3>
      <div class="result-info">
        <p><strong>Serial Number:</strong> {{ scanResult.serialNumber }}</p>
        <p><strong>Product ID:</strong> {{ scanResult.productId }}</p>
        <p><strong>Product Name:</strong> {{ scanResult.productName }}</p>
        <p><strong>Manufacturer:</strong> {{ scanResult.manufacturer }}</p>
        <p><strong>Manufacturing Date:</strong> {{ new Date(scanResult.manufacturingDate).toLocaleString() }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.qr-scanner {
  max-width: 900px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

.scan-section {
  background: #f8fafc;
  padding: 2rem;
  border-radius: 12px;
  margin-bottom: 2rem;
  border: 1px solid #e2e8f0;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #4a5568;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.verify-button {
  background: linear-gradient(45deg, #42b983, #3aa876);
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1.1rem;
  font-weight: 600;
  width: 100%;
  transition: all 0.3s ease;
}

.verify-button:hover:not(:disabled) {
  background: linear-gradient(45deg, #3aa876, #42b983);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.2);
}

.verify-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.result-section {
  margin-top: 2rem;
  padding: 2rem;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  animation: fadeIn 0.5s ease-out;
}

.result-info {
  background: white;
  padding: 1.5rem;
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
</style> 