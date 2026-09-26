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
  accountOperationSchema,
  type AccountOperationFormData,
} from './accountOperation.schema'
import type { Account } from './accounts.types'
import { useAccountOperation } from './useAccountOperation'

const selectClassName = 'h-9 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 aria-invalid:border-destructive'

export function AccountOperationForm({ account }: { account: Account }) {
  const operation = useAccountOperation(account)
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AccountOperationFormData>({
    resolver: zodResolver(accountOperationSchema),
    defaultValues: { operacao: 'depositar' },
  })

  const submit = handleSubmit((data) => {
    operation.reset()
    operation.mutate(data, {
      onSuccess: () => reset({ operacao: data.operacao }),
    })
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle>Nova operação</CardTitle>
        <CardDescription>Realize um depósito ou saque nesta conta.</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="grid gap-4" onSubmit={submit} noValidate>
          <div className="grid gap-2">
            <Label htmlFor="operacao">Operação</Label>
            <select id="operacao" className={selectClassName} {...register('operacao')}>
              <option value="depositar">Depósito</option>
              <option value="sacar">Saque</option>
            </select>
          </div>

          <div className="grid gap-2">
            <Label htmlFor="valor">Valor</Label>
            <Input
              id="valor"
              type="number"
              inputMode="decimal"
              min="0.01"
              step="0.01"
              placeholder="0,00"
              {...register('valor', {
                setValueAs: (value: string) => value === '' ? undefined : Number(value),
              })}
              aria-invalid={Boolean(errors.valor)}
              aria-describedby={errors.valor ? 'valor-error' : undefined}
            />
            {errors.valor && (
              <p id="valor-error" className="text-xs text-destructive">
                {errors.valor.message}
              </p>
            )}
          </div>

          {operation.isError && (
            <Alert variant="destructive">
              <AlertDescription>{operation.error.message}</AlertDescription>
            </Alert>
          )}

          {operation.isSuccess && (
            <Alert className="border-emerald-200 bg-emerald-50 text-emerald-800">
              <AlertDescription className="text-emerald-800">
                Operação realizada com sucesso.
              </AlertDescription>
            </Alert>
          )}

          <Button type="submit" disabled={operation.isPending}>
            {operation.isPending ? 'Processando…' : 'Confirmar operação'}
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}
