import { Navigate, Route, Routes } from 'react-router'
import { AccountCreatePage } from './features/accounts/AccountCreatePage'
import { AccountDetailsPage } from './features/accounts/AccountDetailsPage'
import { DashboardPage } from './features/accounts/DashboardPage'
import { LoginPage } from './features/auth/LoginPage'
import { CustomerCreatePage } from './features/customers/CustomerCreatePage'
import { CustomersPage } from './features/customers/CustomersPage'
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
        <Route path="contas/nova" element={<AccountCreatePage />} />
        <Route path="contas/:accountId" element={<AccountDetailsPage />} />
        <Route path="correntistas" element={<CustomersPage />} />
        <Route path="correntistas/novo" element={<CustomerCreatePage />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App
