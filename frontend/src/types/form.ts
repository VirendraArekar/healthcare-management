export type Field = {
  key: string;
  label: string;
  type?: string;
  optional?: boolean;
  options?: string[];
  lookup?: string;
  valueKey?: string;
  min?: number;
};
