import { Link } from 'react-router'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { AppHeader } from '../../shared/components/AppHeader/AppHeader'
import type { Account } from './accounts.types'
import { useAccounts } from './useAccounts'

const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

const accountTypeLabels: Record<Account['tipo'], string> = {
  ContaCorrente: 'Conta corrente',
  ContaPoupanca: 'Conta poupança',
}

function AccountCard({ account }: { account: Account }) {
  return (
    <Card>
      <CardHeader className="gap-1">
        <CardDescription>{accountTypeLabels[account.tipo]}</CardDescription>
        <CardTitle className="text-base font-medium">Conta {account.numero}</CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-xs text-muted-foreground">Saldo disponível</p>
        <p className="mt-1 text-2xl font-semibold tracking-tight">
          {currencyFormatter.format(account.saldo)}
        </p>
      </CardContent>
      <CardFooter>
        <Button asChild variant="outline" className="w-full">
          <Link to={`/contas/${account.id}`}>Ver extrato</Link>
        </Button>
      </CardFooter>
    </Card>
  )
}

function LoadingAccounts() {
  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3" aria-label="Carregando contas">
      {[0, 1, 2].map((item) => (
        <div key={item} className="h-36 animate-pulse rounded-xl border bg-card" />
      ))}
    </div>
  )
}

export function DashboardPage() {
  const accounts = useAccounts()

  return (
    <div className="min-h-svh bg-muted/40">
      <AppHeader />

      <main className="mx-auto w-full max-w-6xl px-4 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-semibold tracking-tight">Visão geral</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Consulte suas contas e os saldos disponíveis.
          </p>
        </div>

        {accounts.isPending && <LoadingAccounts />}

        {accounts.isError && (
          <Alert variant="destructive">
            <AlertDescription className="flex flex-wrap items-center justify-between gap-3">
              <span>{accounts.error.message}</span>
              {accounts.error.status === 401 && <span>Entre novamente para continuar.</span>}
            </AlertDescription>
          </Alert>
        )}

        {accounts.isSuccess && accounts.data.length === 0 && (
          <Card className="border-dashed">
            <CardHeader>
              <CardTitle className="text-base">Nenhuma conta encontrada</CardTitle>
              <CardDescription>
                Abra uma conta pela API para visualizá-la neste painel.
              </CardDescription>
            </CardHeader>
          </Card>
        )}

        {accounts.isSuccess && accounts.data.length > 0 && (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {accounts.data.map((account) => (
              <AccountCard key={account.id} account={account} />
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
