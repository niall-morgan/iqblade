export interface IOrganisation {
  id?: number;
  companyNumber?: string | null;
  companyName?: string | null;
  website?: string | null;
  status?: string | null;
}

export const defaultValue: Readonly<IOrganisation> = {};
