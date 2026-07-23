import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-error-message',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './error-message.html',
  styleUrl: './error-message.css'
})
export class ErrorMessageComponent {
  message = input<string | null>(null);
}
