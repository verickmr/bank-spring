import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  accountRateSchema,
  type AccountRateFormData,
} from './accountRate.schema'
import type { Account } from './accounts.types'
import { useAccountRate } from './useAccountRate'

export function AccountRateForm({ account }: { account: Account }) {
  const isCurrentAccount = account.tipo === 'ContaCorrente'
  const rate = useAccountRate(account)
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AccountRateFormData>({
    resolver: zodResolver(accountRateSchema),
  })

  const submit = handleSubmit((data) => {
    rate.reset()
    rate.mutate(data, {
      onSuccess: () => reset(),
    })
  })

  return (
    <Card className="mb-6">
      <CardHeader>
        <CardTitle>{isCurrentAccount ? 'Aplicar juros' : 'Aplicar rendimento'}</CardTitle>
        <CardDescription>
          {isCurrentAccount
            ? 'Os juros podem ser aplicados quando o saldo estiver negativo.'
            : 'O rendimento será calculado sobre o saldo atual da poupança.'}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form className="grid gap-4 sm:grid-cols-[1fr_auto] sm:items-end" onSubmit={submit} noValidate>
          <div className="grid gap-2">
            <Label htmlFor="percentual">Taxa percentual</Label>
            <div className="relative">
              <Input
                id="percentual"
                type="number"
                inputMode="decimal"
                min="0.01"
                step="0.01"
                placeholder="Ex.: 2"
                className="pr-10"
                {...register('percentual', {
                  setValueAs: (value: string) => value === '' ? undefined : Number(value),
                })}
                aria-invalid={Boolean(errors.percentual)}
                aria-describedby={errors.percentual ? 'percentual-error' : 'percentual-help'}
              />
              <span className="pointer-events-none absolute inset-y-0 right-3 flex items-center text-sm text-muted-foreground">
                %
              </span>
            </div>
            {errors.percentual ? (
              <p id="percentual-error" className="text-xs text-destructive">
                {errors.percentual.message}
              </p>
            ) : (
              <p id="percentual-help" className="text-xs text-muted-foreground">
                Digite 2 para aplicar uma taxa de 2%.
              </p>
            )}
          </div>

          <Button type="submit" disabled={rate.isPending}>
            {rate.isPending ? 'Aplicando…' : isCurrentAccount ? 'Aplicar juros' : 'Aplicar rendimento'}
          </Button>

          {rate.isError && (
            <Alert variant="destructive" className="sm:col-span-2">
              <AlertDescription>{rate.error.message}</AlertDescription>
            </Alert>
          )}

          {rate.isSuccess && (
            <Alert className="border-emerald-200 bg-emerald-50 text-emerald-800 sm:col-span-2">
              <AlertDescription className="text-emerald-800">
                Taxa aplicada com sucesso.
              </AlertDescription>
            </Alert>
          )}
        </form>
      </CardContent>
    </Card>
  )
}
