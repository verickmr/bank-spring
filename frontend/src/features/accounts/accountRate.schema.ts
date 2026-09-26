import { z } from 'zod'

export const accountRateSchema = z.object({
  percentual: z
    .number({ error: 'Informe um percentual válido.' })
    .positive('O percentual deve ser maior que zero.'),
})

export type AccountRateFormData = z.infer<typeof accountRateSchema>
