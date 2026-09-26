import { z } from 'zod'

export const accountOperationSchema = z.object({
  operacao: z.enum(['depositar', 'sacar']),
  valor: z
    .number({ error: 'Informe um valor válido.' })
    .positive('O valor deve ser maior que zero.'),
})

export type AccountOperationFormData = z.infer<typeof accountOperationSchema>
