declare module 'react-native-date-picker' {
  interface DatePickerProps {
    date: Date;
    onDateChange?: () => void;
    maximumDate?: Date;
    minimumDate?: Date;
    locale?: string;
    mode?: 'datetime' | 'date' | 'time';
  }

  const DatePicker: React.ComponentType<DatePickerProps>;
  export default DatePicker;
}
