import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  customerCreateSchema,
  type CustomerCreateFormData,
} from './customers.schema'
import { useCreateCustomer } from './useCreateCustomer'

function FieldError({ id, message }: { id: string; message?: string }) {
  if (!message) return null

  return (
    <p id={id} className="text-xs text-destructive">
      {message}
    </p>
  )
}

export function CustomerForm() {
  const createCustomer = useCreateCustomer()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CustomerCreateFormData>({
    resolver: zodResolver(customerCreateSchema),
    defaultValues: { cpf: '', nome: '', email: '', senha: '' },
  })

  const submit = handleSubmit((customer) => {
    createCustomer.reset()
    createCustomer.mutate(customer)
  })

  return (
    <form className="grid gap-5" onSubmit={submit} noValidate>
      <div className="grid gap-2">
        <Label htmlFor="nome">Nome completo</Label>
        <Input
          id="nome"
          autoComplete="name"
          maxLength={100}
          {...register('nome')}
          aria-invalid={Boolean(errors.nome)}
          aria-describedby={errors.nome ? 'nome-error' : undefined}
        />
        <FieldError id="nome-error" message={errors.nome?.message} />
      </div>

      <div className="grid gap-2">
        <Label htmlFor="cpf">CPF</Label>
        <Input
          id="cpf"
          inputMode="numeric"
          autoComplete="username"
          maxLength={11}
          placeholder="Somente números"
          {...register('cpf', {
            setValueAs: (value: string) => value.replace(/\D/g, ''),
          })}
          aria-invalid={Boolean(errors.cpf)}
          aria-describedby={errors.cpf ? 'cpf-error' : undefined}
        />
        <FieldError id="cpf-error" message={errors.cpf?.message} />
      </div>

      <div className="grid gap-2">
        <Label htmlFor="email">E-mail</Label>
        <Input
          id="email"
          type="email"
          autoComplete="email"
          {...register('email')}
          aria-invalid={Boolean(errors.email)}
          aria-describedby={errors.email ? 'email-error' : undefined}
        />
        <FieldError id="email-error" message={errors.email?.message} />
      </div>

      <div className="grid gap-2">
        <Label htmlFor="senha">Senha inicial</Label>
        <Input
          id="senha"
          type="password"
          autoComplete="new-password"
          maxLength={72}
          {...register('senha')}
          aria-invalid={Boolean(errors.senha)}
          aria-describedby={errors.senha ? 'senha-error' : undefined}
        />
        <FieldError id="senha-error" message={errors.senha?.message} />
      </div>

      {createCustomer.isError && (
        <Alert variant="destructive">
          <AlertDescription>{createCustomer.error.message}</AlertDescription>
        </Alert>
      )}

      <Button type="submit" className="w-full" disabled={createCustomer.isPending}>
        {createCustomer.isPending ? 'Cadastrando…' : 'Cadastrar correntista'}
      </Button>
    </form>
  )
}
