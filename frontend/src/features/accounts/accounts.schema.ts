import { z } from 'zod'

export const accountCreateSchema = z
  .object({
    tipo: z.enum(['corrente', 'poupanca']),
    numero: z.string().trim().min(1, 'Informe o número da conta.'),
    correntistaId: z
      .number()
      .int()
      .positive('Selecione um correntista.'),
    limite: z
      .number()
      .nonnegative('O limite não pode ser negativo.')
      .optional(),
  })
  .superRefine((account, context) => {
    if (account.tipo === 'corrente' && account.limite === undefined) {
      context.addIssue({
        code: 'custom',
        path: ['limite'],
        message: 'Informe o limite da conta corrente.',
      })
    }
  })

export type AccountCreateFormData = z.infer<typeof accountCreateSchema>
