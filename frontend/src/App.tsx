import { Navigate, Route, Routes } from 'react-router'
import { AccountCreatePage } from './features/accounts/AccountCreatePage'
import { AccountDetailsPage } from './features/accounts/AccountDetailsPage'
import { DashboardPage } from './features/accounts/DashboardPage'
import { LoginPage } from './features/auth/LoginPage'
import { SignupPage } from './features/auth/SignupPage'
import { ProtectedRoute } from './shared/routing/ProtectedRoute'
import { tokenStorage } from './shared/storage/tokenStorage'

function App() {
  const isAuthenticated = Boolean(tokenStorage.get())

  return (
    <Routes>
      <Route
        path="/login"
        element={isAuthenticated ? <Navigate to="/" replace /> : <LoginPage />}
      />
      <Route
        path="/cadastro"
        element={isAuthenticated ? <Navigate to="/" replace /> : <SignupPage />}
      />

      <Route element={<ProtectedRoute />}>
        <Route index element={<DashboardPage />} />
        <Route path="contas/nova" element={<AccountCreatePage />} />
        <Route path="contas/:accountId" element={<AccountDetailsPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App
