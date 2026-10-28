CREATE UNIQUE INDEX patient_number_unique ON records((data->>'patientNumber')) WHERE kind='patient';
