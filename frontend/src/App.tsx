import { Navigate, Route, Routes } from 'react-router'
import { DashboardPage } from './features/accounts/DashboardPage'
import { LoginPage } from './features/auth/LoginPage'
import { ProtectedRoute } from './shared/routing/ProtectedRoute'
import { tokenStorage } from './shared/storage/tokenStorage'

function App() {
  return (
    <Routes>
      <Route
        path="/login"
        element={tokenStorage.get() ? <Navigate to="/" replace /> : <LoginPage />}
      />

      <Route element={<ProtectedRoute />}>
        <Route index element={<DashboardPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App
