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
import { useCustomers } from '../customers/useCustomers'

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
  const customers = useCustomers()
  const createAccount = useCreateAccount()
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
      correntistaId: 0,
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

  if (customers.isPending) {
    return <div className="h-64 animate-pulse rounded-lg bg-muted" aria-label="Carregando correntistas" />
  }

  if (customers.isError) {
    return (
      <Alert variant="destructive">
        <AlertDescription>{customers.error.message}</AlertDescription>
      </Alert>
    )
  }

  if (customers.data.length === 0) {
    return (
      <Alert>
        <AlertDescription>Cadastre um correntista antes de abrir uma conta.</AlertDescription>
      </Alert>
    )
  }

  return (
    <form className="grid gap-5" onSubmit={submit} noValidate>
      <div className="grid gap-2">
        <Label htmlFor="correntistaId">Correntista</Label>
        <select
          id="correntistaId"
          className={selectClassName}
          {...register('correntistaId', {
            setValueAs: (value: string) => Number(value),
          })}
          aria-invalid={Boolean(errors.correntistaId)}
          aria-describedby={errors.correntistaId ? 'correntista-error' : undefined}
        >
          <option value={0}>Selecione um correntista</option>
          {customers.data.map((customer) => (
            <option key={customer.id} value={customer.id}>
              {customer.nome} · {customer.cpf}
            </option>
          ))}
        </select>
        <FieldError id="correntista-error" message={errors.correntistaId?.message} />
      </div>

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
