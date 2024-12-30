<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <header>
    <nav class="nav-bar">
      <router-link to="/" class="nav-link">QR Generator</router-link>
      <router-link to="/scanner" class="nav-link">QR Scanner</router-link>
    </nav>
  </header>

  <RouterView />
</template>

<style scoped>
.nav-bar {
  background: white;
  padding: 1rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  gap: 2rem;
}

.nav-link {
  color: #2d3748;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background: #f7fafc;
  color: #42b983;
}

.nav-link.router-link-active {
  background: #42b983;
  color: white;
}
</style>
