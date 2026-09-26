import { Link, useLocation } from 'react-router'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { AppHeader } from '../../shared/components/AppHeader/AppHeader'
import type { Customer } from './customers.types'
import { useCustomers } from './useCustomers'

function formatCpf(cpf: string) {
  return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4')
}

function CustomerCard({ customer }: { customer: Customer }) {
  const accountsLabel = customer.contas.length === 1 ? '1 conta' : `${customer.contas.length} contas`

  return (
    <Card>
      <CardHeader>
        <CardTitle>{customer.nome}</CardTitle>
        <CardDescription>{formatCpf(customer.cpf)}</CardDescription>
      </CardHeader>
      <CardContent className="space-y-1">
        <p className="break-all text-sm">{customer.email}</p>
        <p className="text-sm text-muted-foreground">{accountsLabel}</p>
      </CardContent>
    </Card>
  )
}

function LoadingCustomers() {
  return (
    <div className="grid gap-4 sm:grid-cols-2" aria-label="Carregando correntistas">
      {[0, 1, 2, 3].map((item) => (
        <div key={item} className="h-36 animate-pulse rounded-xl border bg-card" />
      ))}
    </div>
  )
}

export function CustomersPage() {
  const customers = useCustomers()
  const location = useLocation()
  const customerCreated = Boolean(
    (location.state as { customerCreated?: boolean } | null)?.customerCreated,
  )

  return (
    <div className="min-h-svh bg-muted/40">
      <AppHeader />

      <main className="mx-auto w-full max-w-6xl px-4 py-8">
        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">Correntistas</h1>
            <p className="mt-1 text-sm text-muted-foreground">
              Consulte os clientes cadastrados e a quantidade de contas vinculadas.
            </p>
          </div>
          <Button asChild>
            <Link to="/correntistas/novo">Novo correntista</Link>
          </Button>
        </div>

        {customerCreated && (
          <Alert className="mb-6 border-emerald-200 bg-emerald-50 text-emerald-800">
            <AlertDescription className="text-emerald-800">
              Correntista cadastrado com sucesso.
            </AlertDescription>
          </Alert>
        )}

        {customers.isPending && <LoadingCustomers />}

        {customers.isError && (
          <Alert variant="destructive">
            <AlertDescription>{customers.error.message}</AlertDescription>
          </Alert>
        )}

        {customers.isSuccess && customers.data.length === 0 && (
          <Card className="border-dashed">
            <CardHeader>
              <CardTitle className="text-base">Nenhum correntista encontrado</CardTitle>
              <CardDescription>Os clientes cadastrados aparecerão nesta página.</CardDescription>
            </CardHeader>
          </Card>
        )}

        {customers.isSuccess && customers.data.length > 0 && (
          <div className="grid gap-4 sm:grid-cols-2">
            {customers.data.map((customer) => (
              <CustomerCard key={customer.id} customer={customer} />
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
