import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-placeholder',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './placeholder.html',
  styleUrl: './placeholder.css'
})
export class PlaceholderComponent {
  title = input.required<string>();
}
