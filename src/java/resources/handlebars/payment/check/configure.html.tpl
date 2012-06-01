{{#errors}}
<div class="alert alert-error">
There are validation errors
</div>
{{/errors}}

<label>Order to: </label>
<span class="help-block">This information will be displayed on the check payment page.</span>
<input type="text" class="span6" name="orderto" value="{{configuration.orderto}}"  placeholder="The name of the company or individual checks must be ordered to" required>
{{#if errors.orderto}}
<div class="error">{{errors.orderto}}</div>
{{/if}}

<label>Address :</label>
<textarea name="sendto" class="span6" rows="6" required placeholder="Enter here the address the customers will have to send the address to">{{configuration.sendto}}</textarea>
{{#if errors.sendto}}
<div class="error">{{errors.sendto}}</div>
{{/if}}