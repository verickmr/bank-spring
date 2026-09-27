import { zodResolver } from '@hookform/resolvers/zod'
import { useForm, useWatch } from 'react-hook-form'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  accountCreateSchema,
  type AccountCreateFormData,
} from './accounts.schema'
import { useCreateAccount } from './useCreateAccount'
import { useCurrentCustomer } from '../customers/useCurrentCustomer'

const selectClassName = 'h-9 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 aria-invalid:border-destructive'

function FieldError({ id, message }: { id: string; message?: string }) {
  if (!message) return null

  return (
    <p id={id} className="text-xs text-destructive">
      {message}
    </p>
  )
}

export function AccountForm() {
  const customer = useCurrentCustomer()
  const createAccount = useCreateAccount(customer.data?.id ?? 0)
  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<AccountCreateFormData>({
    resolver: zodResolver(accountCreateSchema),
    defaultValues: {
      tipo: 'corrente',
      numero: '',
      limite: undefined,
    },
  })
  const accountType = useWatch({ control, name: 'tipo' })

  const submit = handleSubmit((account) => {
    createAccount.reset()
    createAccount.mutate(
      account.tipo === 'poupanca'
        ? { ...account, limite: undefined }
        : account,
    )
  })

  if (customer.isPending) {
    return <div className="h-64 animate-pulse rounded-lg bg-muted" aria-label="Carregando cadastro" />
  }

  if (customer.isError) {
    return (
      <Alert variant="destructive">
        <AlertDescription>{customer.error.message}</AlertDescription>
      </Alert>
    )
  }

  return (
    <form className="grid gap-5" onSubmit={submit} noValidate>
      <div className="grid gap-2">
        <Label htmlFor="tipo">Tipo de conta</Label>
        <select id="tipo" className={selectClassName} {...register('tipo')}>
          <option value="corrente">Conta corrente</option>
          <option value="poupanca">Conta poupança</option>
        </select>
      </div>

      <div className="grid gap-2">
        <Label htmlFor="numero">Número da conta</Label>
        <Input
          id="numero"
          placeholder="Ex.: 0001-01"
          {...register('numero')}
          aria-invalid={Boolean(errors.numero)}
          aria-describedby={errors.numero ? 'numero-error' : undefined}
        />
        <FieldError id="numero-error" message={errors.numero?.message} />
      </div>

      {accountType === 'corrente' && (
        <div className="grid gap-2">
          <Label htmlFor="limite">Limite</Label>
          <Input
            id="limite"
            type="number"
            inputMode="decimal"
            min="0"
            step="0.01"
            placeholder="0,00"
            {...register('limite', {
              setValueAs: (value: string) => value === '' ? undefined : Number(value),
            })}
            aria-invalid={Boolean(errors.limite)}
            aria-describedby={errors.limite ? 'limite-error' : undefined}
          />
          <FieldError id="limite-error" message={errors.limite?.message} />
        </div>
      )}

      {createAccount.isError && (
        <Alert variant="destructive">
          <AlertDescription>{createAccount.error.message}</AlertDescription>
        </Alert>
      )}

      <Button type="submit" className="w-full" disabled={createAccount.isPending}>
        {createAccount.isPending ? 'Abrindo conta…' : 'Abrir conta'}
      </Button>
    </form>
  )
}
