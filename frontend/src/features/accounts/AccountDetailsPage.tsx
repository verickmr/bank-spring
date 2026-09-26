import { Link, useParams } from 'react-router'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { TransactionList } from '../transactions/TransactionList'
import { useTransactions } from '../transactions/useTransactions'
import { AppHeader } from '../../shared/components/AppHeader/AppHeader'
import { AccountOperationForm } from './AccountOperationForm'
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

export function AccountDetailsPage() {
  const { accountId: accountIdParam } = useParams()
  const accountId = Number(accountIdParam)
  const isValidAccountId = Number.isInteger(accountId) && accountId > 0
  const accounts = useAccounts()
  const account = accounts.data?.find((item) => item.id === accountId)
  const transactions = useTransactions(
    accountId,
    isValidAccountId && accounts.isSuccess && Boolean(account),
  )

  const accountError = !isValidAccountId
    ? 'Identificador de conta inválido.'
    : accounts.isError
      ? accounts.error.message
      : accounts.isSuccess && !account
        ? 'Conta não encontrada.'
        : undefined

  return (
    <div className="min-h-svh bg-muted/40">
      <AppHeader />

      <main className="mx-auto w-full max-w-4xl px-4 py-8">
        <Button asChild variant="ghost" className="mb-4">
          <Link to="/">← Voltar para contas</Link>
        </Button>

        {accounts.isPending && (
          <div className="h-40 animate-pulse rounded-xl border bg-card" aria-label="Carregando conta" />
        )}

        {accountError && (
          <Alert variant="destructive">
            <AlertDescription>{accountError}</AlertDescription>
          </Alert>
        )}

        {account && (
          <>
            <div className="mb-6 grid gap-6 md:grid-cols-2">
              <Card>
                <CardHeader>
                  <CardDescription>{accountTypeLabels[account.tipo]}</CardDescription>
                  <CardTitle>Conta {account.numero}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">Saldo disponível</p>
                  <p className="mt-1 text-3xl font-semibold tracking-tight">
                    {currencyFormatter.format(account.saldo)}
                  </p>
                </CardContent>
              </Card>

              <AccountOperationForm account={account} />
            </div>

            <TransactionList
              transactions={transactions.data}
              isPending={transactions.isPending}
              errorMessage={transactions.error?.message}
            />
          </>
        )}
      </main>
    </div>
  )
}
